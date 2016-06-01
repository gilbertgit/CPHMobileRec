package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.support.v4.app.LoaderManager;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;

/**
 * Created by titan on 5/21/16.
 */
public class RescanActivity extends TabActivity  implements EMDKManager.EMDKListener {

    private BroadcastReceiver EMDKRescanReceiver;
    private ProfileManager mProfileManager = null;
    private String profileName = "CPHMobile";

    int dealershipSelection = 0;
    String selectedDealership;

    ArrayList dealershipList = new ArrayList();
    ArrayAdapter<Dealership> dealershipAdapter;
    final HashMap<String, String> spinnerDealershipMap = new HashMap<String, String>();

    Spinner spinnerDealership;
    DBHelper dbHelper;
    private FragmentTabHost mTabHost;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescan);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>RESCAN</font>"));
        actionBar.show();

        dbHelper = new DBHelper(RescanActivity.this);
        dbHelper.getWritableDatabase();

        spinnerDealership = (Spinner)findViewById(R.id.spinnerDealership);

        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);


        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("Tab1", getResources().getDrawable(R.drawable.oval));
        tab1.setContent(new Intent(this,TabRescanActivity.class));

        tab2.setIndicator("Tab2");
        tab2.setContent(new Intent(this,TabRescanDoneActivity.class));


        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

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
                        String barcode = Utilities.CheckVinSpecialCases(data);

                        NotifyTabs(barcode);
                    }

                }

            }

        };
        this.registerReceiver(EMDKRescanReceiver, intentFilter);
        GetDealershipsDB();
    }

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
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void NotifyTabs(String vin)
    {
        Intent i = new Intent("com.cphandheld.CPHMobileRec.TAB_DATA_REFRESH");
        i.putExtra("vin", vin);
        sendBroadcast(i);
    }

    public void GetDealershipsDB() {
        Cursor c = DBUsers.getDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
        dealershipList = new ArrayList(c.getCount());
        spinnerDealershipMap.clear();

        if (c.moveToFirst()) {
            do {

                Dealership d = DBUsers.setDealershipData(c);

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

                    //GetPhysicalDB(false);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
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
}
