package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.support.v4.app.FragmentTabHost;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by titan on 5/21/16.
 */
public class RescanActivity extends TabActivity  implements EMDKManager.EMDKListener {

    private final String TAG = "RescanActivity";
    private BroadcastReceiver EMDKRescanReceiver;
    private ProfileManager mProfileManager = null;
    private String profileName = "CPHMobile";

    String rescanData;
    String errorMessage;

    int dealershipSelection = 0;
    String selectedDealership;
    JSONArray rescanResults;
    ProgressDialog mProgressDialog;
    ArrayList dealershipList = new ArrayList();
    ArrayAdapter<Dealership> dealershipAdapter;
    final HashMap<String, String> spinnerDealershipMap = new HashMap<String, String>();

    Spinner spinnerDealership;
    private TextView textRescanCount;
    DBHelper dbHelper;
    TabHost tabHost;
    private EMDKManager emdkManager = null;

    private GPSHelper gpsHelper;
    Intent gpsServiceIntent;
    private Location lastKnownLoc;
    private String latitude;
    private String longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescan);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>RESCAN</font>"));
        actionBar.show();

        dbHelper = new DBHelper(RescanActivity.this);
        dbHelper.getWritableDatabase();

        mProgressDialog = new ProgressDialog(RescanActivity.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("Fetching Rescan...");
        mProgressDialog.setMessage("Hold on a sec...");

        spinnerDealership = (Spinner)findViewById(R.id.spinnerDealership);
        textRescanCount = (TextView)findViewById(R.id.textRescanCount);

        // create the TabHost that will contain the Tabs
        tabHost = (TabHost)findViewById(android.R.id.tabhost);


        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("Rescan", getResources().getDrawable(R.drawable.oval));
        tab1.setContent(new Intent(this,TabRescanActivity.class));

        tab2.setIndicator("Completed");
        tab2.setContent(new Intent(this,TabRescanDoneActivity.class));

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

        // Create instance of service so we have context
        gpsHelper = new GPSHelper(RescanActivity.this);

        // check if GPS enabled
        if (!gpsHelper.canGetLocation()) {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsHelper.showSettingsAlert();
        }

        gpsServiceIntent = new Intent(this, GPSHelper.class);
        startService(gpsServiceIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mGPSReceiver, new IntentFilter(getString(R.string.intent_gps_receiver)));
    }

    public TextView getTextRescanCount()
    {
        return textRescanCount;
    }

    @Override
    protected void onResume() {
// TODO Auto-generated method stub
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(getString(R.string.scan_intent));
        //Create a our Broadcast Receiver.
        EMDKRescanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Get the source of the data

                String source = intent.getStringExtra(getString(R.string.datawedge_source));

                //Check if the data has come from the barcode scanner
                if (source.equalsIgnoreCase("scanner")) {
                    //Get the data from the intent
                    String data = intent.getStringExtra(getString(R.string.datawedge_data_string));

                    //Check that we have received data
                    if (data != null && data.length() > 0) {
                        String vin = Utilities.CheckVinSpecialCases(data);

                        NotifyTabsOfUpdate(vin, getString(R.string.intent_data_refresh));
                    }

                }

            }

        };
        this.registerReceiver(EMDKRescanReceiver, intentFilter);
        GetDealershipsDB();

        //int rescanCompleteCount = DBRescan.getRescanCompletedCountByDealerCode(dbHelper, Utilities.currentDealership);

    }

    private BroadcastReceiver mGPSReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Status");
            Bundle b = intent.getBundleExtra("Location");
            lastKnownLoc = (Location) b.getParcelable("Location");
            if (lastKnownLoc != null) {
                latitude = String.valueOf(lastKnownLoc.getLatitude());
                longitude = String.valueOf(lastKnownLoc.getLongitude());

            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        //Register our receiver.
        this.unregisterReceiver(this.EMDKRescanReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rescan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_manual:
                Intent i = new Intent(RescanActivity.this, ManualEntryActivity.class);
                i.putExtra("extraAppType", "RESCAN");
                i.putExtra("extraDealership", selectedDealership);
                i.putExtra("extraDealershipIndex", spinnerDealership.getSelectedItemPosition());
                startActivityForResult(i, 2);
                break;

            case R.id.action_sync_rescan:
                SyncRescans();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void SyncRescans()
    {
        // check if there are rescans to upload
        if(DBRescan.uploadReady(dbHelper))
        {
            // Upload rescans
            new ccSubmitRescan().execute();
            // After this completes we need get get rescans
        }
        else
            // nothing to upload, get rescans
            new ccGetRescan().execute();
    }

    private class ccSubmitRescan extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            return Utilities.hasInternetAccess(RescanActivity.this);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            {
                new SubmitRescanTask().execute();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Network Error. Check Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ccGetRescan extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            return Utilities.hasInternetAccess(RescanActivity.this);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            {
                new GetRescanTask().execute();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Network Error. Check Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void NotifyTabsOfUpdate(String vin, String action)
    {
        Intent i = new Intent();
        i.setAction(action);
        i.putExtra("vin", vin);
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        i.putExtra("method", "Scanned");
        sendBroadcast(i);
    }

    private void NotifyTabsDealershipChange(String dealership)
    {
        Intent i = new Intent(getString(R.string.intent_dealership_change));
        i.putExtra("dealership", dealership);
        sendBroadcast(i);
    }

    private void NotifyTabsRescanSync()
    {
        Intent i = new Intent(getString(R.string.intent_rescan_sync));
        //i.putExtra("dealership", dealership);
        sendBroadcast(i);
    }

    public void GetDealershipsDB() {

        Cursor c;
        if(DBUsers.hasFilteredDealerships(dbHelper))
            c = DBUsers.getFilteredDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
        else
            c = DBUsers.getDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));

        dealershipList = new ArrayList(c.getCount());
        spinnerDealershipMap.clear();

        if (c.moveToFirst()) {
            do {

                Dealership d = DBUsers.setDealershipData(c);
                if(c.getPosition() == 0)
                    Utilities.currentDealership = d.DealerCode;

                dealershipList.add(d);

                spinnerDealershipMap.put(d.Name, d.DealerCode);

            } while (c.moveToNext());
        }
        c.close();

        if (dealershipList != null && dealershipList.size() > 0) {
            //lotAdapter.notifyDataSetChanged();

            dealershipAdapter = new ArrayAdapter<Dealership>(this, R.layout.generic_list, dealershipList);
            dealershipAdapter.setDropDownViewResource(R.layout.generic_list);
            spinnerDealership.setAdapter(dealershipAdapter);

            if (dealershipSelection == 0)
                spinnerDealership.setSelection(0);
            else {
                Log.v("dealershipSelection:", String.valueOf(dealershipSelection));
                spinnerDealership.setSelection(dealershipSelection, true);
            }

            spinnerDealership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    selectedDealership = spinnerDealershipMap.get(spinnerDealership.getSelectedItem().toString());

                    Utilities.currentDealership = selectedDealership;
                    NotifyTabsDealershipChange(selectedDealership);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                    Log.v(TAG, "onNothingSelected");
                }
            });
            NotifyTabsDealershipChange(Utilities.currentDealership);
        }
    }

    private class SubmitRescanTask extends AsyncTask<String, Void, Boolean> {
        ArrayList rescan;
        @Override
        protected void onPreExecute() {

            mProgressDialog.setTitle("Submitting Rescan...");
            mProgressDialog.setMessage("Hold on a sec...");
            mProgressDialog.show();


            rescanData = "";
            Gson gson = new Gson();
            rescan = DBRescan.getRescanForUpload(dbHelper, String.valueOf(Utilities.currentUser.Id));

            if (!rescan.equals(null) && rescan.size() != 0) {
                rescanData += "{\"ScannerUserId\":\"" + Utilities.currentUser.Id + "\",\"ScannerSerialNumber\":\"" + Utilities.androidId + "\",\"Rescans\":[";
                for (int i = 0; i < rescan.size(); i++) {
                    String json = gson.toJson(rescan.get(i));
                    rescanData += json;
                    if (i != rescan.size() - 1)
                        rescanData += ",";
                }
                rescanData += "]}";
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            return SubmitRescan();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result)
            {
                if(dbHelper.BackupRescanDB(dbHelper, getApplicationContext(), String.valueOf(Utilities.currentUser.Id)))
                {
                    // Remove the rescan that we uploaded
                    for (int i = 0; i < rescan.size(); i++) {
                        DBRescan.deleteRescan(dbHelper, ((RescanComplete) rescan.get(i)).getSIID());
                    }
                    Toast.makeText(getApplicationContext(), "Successfully upload " + rescan.size() + " rescans!", Toast.LENGTH_LONG).show();
                    NotifyTabsRescanSync();
                }
            }
            else
            {
                if(!errorMessage.equals(""))
                {
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                }
            }
            mProgressDialog.dismiss();
        }

        private boolean SubmitRescan() {

            URL url;
            HttpURLConnection connection;
            OutputStreamWriter request;
            InputStreamReader isr;
            JSONObject responseData;
            String result;

            try {
                if (!rescanData.equals(null) && rescanData != "") {

                    String json = rescanData;
                    String address = Utilities.AppURL + Utilities.RescanUploadURL;
                    url = new URL(address);

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setFixedLengthStreamingMode(json.length());
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("Content-type", "application/json");
                    connection.setRequestMethod("POST");

                    request = new OutputStreamWriter(connection.getOutputStream());
                    request.write(json);
                    request.flush();
                    request.close();

                    int code = connection.getResponseCode();

                    if (code == 200) {

                        isr = new InputStreamReader(connection.getInputStream());
                        result = Utilities.StreamToString(isr);
                        return true;
                    } else {
                        isr = new InputStreamReader(connection.getErrorStream());
                        result = Utilities.StreamToString(isr);
                        responseData = new JSONObject(result);
                        errorMessage = responseData.getString("Message");
                        Log.i("vehicle check in error", errorMessage);
                        return false;

                    }
                }
            } catch (JSONException | IOException e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            }

           return false;
        }

    }

    private class GetRescanTask extends AsyncTask<String, Void, Boolean> {

        String dealercodes = "";

        @Override
        protected void onPreExecute() {
            List<Dealership> dealerships = dealershipList;
            int i = 0;
            if(dealerships != null) {
                int length = dealerships.size();
                for (Dealership d : dealerships) {
                    i++;
                    if (i != length)
                        dealercodes += d.DealerCode + ",";
                    else
                        dealercodes += d.DealerCode;

                }
            }
            mProgressDialog.setTitle("Fetching Rescan...");
            mProgressDialog.setMessage("Hold on a sec...");
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            //Utilities.currentUser = null;

            return GetRescans();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {
                // Clear the rescans for this user
                DBRescan.deleteRescanByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));

                // Create array from rescan json result
                ArrayList<Rescan> array = new Gson().fromJson(rescanResults.toString(), new TypeToken<List<Rescan>>() {
                }.getType());

                for (Rescan r : array) {
                    // insert rescan
                    // siid, dealerCode, vin, assigned, year, make, model, color,entryMethod, scannedDate, userId
                    DBRescan.insertRescan(dbHelper, r.getSIID(), r.getDealership(), r.getVIN(), r.getAssigned(), r.getYear(), r.getMake(),                           r.getModel(), r.getColor(), r.getEntryType(), r.getScanneDate(), r.getUserId());
                }

                NotifyTabsOfUpdate("", getString(R.string.intent_data_refresh));
                Toast.makeText(getApplicationContext(), "Rescan fetched.", Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
        }

        private boolean GetRescans() {

            URL url;
            HttpURLConnection connection;
            JSONObject responseData;
            InputStreamReader isr;
            String result;
            String errorMessage;

            try {
                dealercodes = URLEncoder.encode(dealercodes, "utf-8");
                String address = Utilities.AppURL + Utilities.RescanDownloadURL + Utilities.androidId + "/" + dealercodes;
                url = new URL(address);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    isr = new InputStreamReader(connection.getInputStream());
                    result = Utilities.StreamToString(isr);

                    rescanResults = new JSONArray(result);
                    return true;

                } else {
                    isr = new InputStreamReader(connection.getErrorStream());
                    result = Utilities.StreamToString(isr);
                    responseData = new JSONObject(result);
                    errorMessage = responseData.getString("Message");
                    Log.i("FETCH RESCAN ERROR", errorMessage);
                    return false;
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }


            return false;
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
        mProfileManager = (ProfileManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE);

        if (mProfileManager != null) {
            try {

                String[] modifyData = new String[1];
                //Call processProfile with profile name and SET flag to create the profile. The modifyData can be null.

                EMDKResults results = mProfileManager.processProfile(profileName, ProfileManager.PROFILE_FLAG.SET, modifyData);
                if (results.statusCode == EMDKResults.STATUS_CODE.FAILURE) {
                    //Failed to set profile
                    Log.v("TabRescanActivity", "Failed to set profile");
                }
            } catch (Exception ex) {
                // Handle any exception
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClosed() {

    }

    @Override
    protected void onDestroy()
    {
        Log.v(TAG, "onDestroy");

        gpsHelper.stopUsingGPS();

        stopService(gpsServiceIntent);

//        if(emdkManager != null)
//            emdkManager.release();

        super.onDestroy();
    }
}
