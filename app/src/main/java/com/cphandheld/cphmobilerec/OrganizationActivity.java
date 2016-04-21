package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by titan on 4/7/16.
 */
public class OrganizationActivity  extends Activity
{
    public static final String PREFS_FILE = "SharedPrefs";
    ListView listOrganizations;
    ArrayList orgs;
    private ProgressDialog mProgressDialog;
    DBHelper dbHelper;
    Button buttonImportData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);

        //setHeader(R.color.colorHeader, getResources().getString(R.string.hello_admin), "", R.string.org_header);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Select Organization");
        actionBar.show();

        dbHelper = new DBHelper(OrganizationActivity.this);
        dbHelper.getWritableDatabase();

        mProgressDialog = new ProgressDialog(OrganizationActivity.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("Fetching organizations...");
        mProgressDialog.setMessage("Just hold on a sec...");

        SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        listOrganizations = (ListView) findViewById(R.id.listOrganizations);
        listOrganizations.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView textView = (TextView) view.findViewById(R.id.rowTextView);
                textView.setTextColor(getResources().getColor(R.color.colorOrgTextSelected));
                view.setBackgroundColor(getResources().getColor(R.color.colorOrgBgSelected));

                Organization org = (Organization)orgs.get(position);
                int orgId = org.organizationId;
                String name = org.name;

                SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("orgId", orgId);
                editor.putString("orgName", name);
                editor.commit();

                Intent i = new Intent(OrganizationActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        buttonImportData = (Button)findViewById(R.id.buttonImportData);
        buttonImportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.ImportData(getApplicationContext());
            }
        });

        if(Utilities.isNetworkAvailable(OrganizationActivity.this)) {
            new loadOrganizations().execute();
        }
        else
        {
            GetOrganizationsDB();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    public void onBackPressed()
    {
        Toast toast = Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 75);
        toast.show();

        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void GetOrganizationsDB()
    {
        Cursor c = DBOrganizations.getOrganizations(dbHelper);
        orgs = new ArrayList(c.getCount());

        if (c.moveToFirst()) {
            do {

                Organization org = new Organization();

                int nameIndex = c.getColumnIndex("name");
                org.name = c.getString(nameIndex);

                int organizationIdIndex = c.getColumnIndex("organizationId");
                org.organizationId = c.getInt(organizationIdIndex);

                orgs.add(org);
            } while (c.moveToNext());
        }
        c.close();

        if (orgs != null && orgs.size() > 0) {
            ArrayAdapter<Organization> adapter = new ArrayAdapter<Organization>(OrganizationActivity.this, R.layout.generic_list, orgs);
            listOrganizations.setAdapter(adapter);
        }
        mProgressDialog.dismiss();
    }

    private class loadOrganizations extends AsyncTask<String, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            getOrganizations();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
//            if (orgs != null && orgs.size() > 0) {
//                ArrayAdapter<Organization> adapter = new ArrayAdapter<Organization>(OrganizationActivity.this, R.layout.generic_list, orgs);
//                listOrganizations.setAdapter(adapter);
//            }
//            mProgressDialog.dismiss();
            GetOrganizationsDB();
        }

        private void getOrganizations() {
            HttpURLConnection connection;
            InputStreamReader isr;
            URL url;
            String result;
            JSONArray responseData;

            try {
                String address = Utilities.AppURL + Utilities.OrganizationsURL;
                url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                isr = new InputStreamReader(connection.getInputStream());

                if(connection.getResponseCode() == 200) {
                    DBOrganizations.clearOrganizationTable(dbHelper);
                    //dbHelper.clearOrganizationTable();
                    result = Utilities.StreamToString(isr);
                    responseData = new JSONArray(result);

                    orgs = new ArrayList(responseData.length());

                    for (int i = 0; i < responseData.length(); i++) {
                        JSONObject temp = responseData.getJSONObject(i);

                        DBOrganizations.insertOrganization(dbHelper, temp.getInt("OrganizationId"), temp.getString("Name"));
                        //dbHelper.insertOrganization(temp.getInt("OrganizationId"), temp.getString("Name"));
                    }
//                    result = Utilities.StreamToString(isr);
//                    responseData = new JSONArray(result);
//
//                    orgs = new ArrayList(responseData.length());
//
//                    for (int i = 0; i < responseData.length(); i++) {
//                        Organization org = new Organization();
//                        JSONObject temp = responseData.getJSONObject(i);
//                        org.name = temp.getString("Name");
//                        org.organizationId = temp.getInt("OrganizationId");
//                        orgs.add(org);
//                    }
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                Log.i("getOrganizations()","error...");
            }
        }
    }

    private class Organization
    {
        private String name;
        private int organizationId;

        public Organization()
        {

        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
