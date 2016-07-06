package com.cphandheld.cphmobilerec;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

/**
 * Created by titan on 5/29/16.
 */
public class TabRescanActivity extends Activity {
    private final String TAG = "TabRescanActivity";
    UpdaterBroadcastReceiver updaterBroadcastReceiver;
    private ArrayList<Rescan> rescans;
    private RescanListAdapter listAdapter;
    private ListView rescanListView;
    private String vinReceived = "";
    DBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_rescans);

        dbHelper = new DBHelper(TabRescanActivity.this);
        dbHelper.getWritableDatabase();

        rescanListView = (ListView)findViewById(R.id.listRescans);

        rescans = new ArrayList();
        listAdapter = new RescanListAdapter(TabRescanActivity.this, 0, rescans, mTouchListener);
        rescanListView.setAdapter(listAdapter);

    }

    @Override
    public void onResume()
    {
        AddRescansToListView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.intent_data_refresh));
        updaterBroadcastReceiver = new UpdaterBroadcastReceiver();
        registerReceiver(updaterBroadcastReceiver,filter);
        super.onResume();
        Log.v("TabRescanActivity", "onResume");
    }

    @Override
    protected void onPause() {
        unregisterReceiver(this.updaterBroadcastReceiver);
        super.onPause();
    }

    private class UpdaterBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "UpdaterBroadcastReceiver");
            vinReceived = intent.getStringExtra("vin");
            if(!vinReceived.equals("")) {
                if (RemoveRescan(vinReceived)) {
                    listAdapter.notifyDataSetChanged();
                }
            }else
            {
                AddRescansToListView();
            }
        }
    }

    private boolean RemoveRescan(String vin)
    {
        boolean dataUpdated = false;
        String firstName = Utilities.currentUser.FirstName;
        String lastName = Utilities.currentUser.LastName;
        String scannedBy =  firstName + " " + lastName;
        ArrayList<Rescan> rescansToRemove = new ArrayList<Rescan>();

        for(Rescan r: rescans) {
            if (r.getVIN().equals(vin)) {

                rescansToRemove.add(r);
                dataUpdated = true;
            }
        }

        if(dataUpdated) {
            rescans.removeAll(rescansToRemove);
            DBRescan.updateRescanByVin(dbHelper, vin, "Scanned", Utilities.GetDateTimeString(), scannedBy, String.valueOf(Utilities.currentUser.Id));
        }
        return dataUpdated;
    }

    private void InsetTempData()
    {
        DBRescan.deleteRescan(dbHelper);
        rescans.clear();
    }

    private void AddRescansToListView()
    {
        rescans.clear();
        Cursor c = DBRescan.getRescanToScan(dbHelper, "0000A");

        if (c.moveToFirst()) {
            do {

               Rescan r = DBRescan.setRescanData(c);
                rescans.add(r);
            }while (c.moveToNext());

        }
        c.close();

        listAdapter.notifyDataSetChanged();
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };



}
