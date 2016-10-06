package com.cphandheld.cphmobilerec;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by titan on 5/29/16.
 */
public class TabRescanDoneActivity extends Activity {

    private final String TAG = "TabRescanDoneActivity";
    DBHelper dbHelper;
    private ArrayList<Rescan> completedRescans;
    private RescanListAdapter listAdapter;
    private ListView doneRescanListView;
    UpdaterBroadcastReceiver updaterBroadcastReceiver;
    private String vinReceived = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_rescans_done);

        dbHelper = new DBHelper(TabRescanDoneActivity.this);
        dbHelper.getWritableDatabase();


        doneRescanListView = (ListView) findViewById(R.id.listDoneRescans);

        completedRescans = new ArrayList();
        listAdapter = new RescanListAdapter(TabRescanDoneActivity.this, 0, completedRescans, mTouchListener);
        doneRescanListView.setAdapter(listAdapter);

//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mGPSReceiver, new IntentFilter(getString(R.string.intent_gps_receiver)));

    }

    private void SetRescanCount()
    {
        int rescanCompleteCount = DBRescan.getRescanCompletedCountByDealerCode(dbHelper, Utilities.currentDealership);
        RescanActivity activity = (RescanActivity) this.getParent();
        TextView test = (TextView)activity.findViewById(R.id.textRescanCount);
        test.setText("Count(" + rescanCompleteCount + ")");
    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.intent_rescan_sync));
        filter.addAction(getString(R.string.intent_dealership_change));
        updaterBroadcastReceiver = new UpdaterBroadcastReceiver();
        registerReceiver(updaterBroadcastReceiver, filter);
        super.onResume();
        AddRescansToListView();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(this.updaterBroadcastReceiver);
        super.onPause();
    }

//    private BroadcastReceiver mGPSReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            String message = intent.getStringExtra("Status");
//            Bundle b = intent.getBundleExtra("Location");
//            lastKnownLoc = (Location) b.getParcelable("Location");
//            if (lastKnownLoc != null) {
//                latitude = String.valueOf(lastKnownLoc.getLatitude());
//                longitude = String.valueOf(lastKnownLoc.getLongitude());
//
//            }
//        }
//    };

    private class UpdaterBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "UpdaterBroadcastReceiver");
            String action = intent.getAction();
            if (action.equals(getString(R.string.intent_rescan_sync))) {
                completedRescans.clear();
                listAdapter.notifyDataSetChanged();
                SetRescanCount();
            }
            else if (action.equals(getString(R.string.intent_dealership_change)))
            {
                AddRescansToListView();
            }
        }
    }

    private void AddRescansToListView() {
        completedRescans.clear();
        Cursor c = DBRescan.getCompletedRescans(dbHelper, Utilities.currentDealership);

        if (c.moveToFirst()) {
            do {

                Rescan r = DBRescan.setRescanData(c);
                completedRescans.add(r);
            } while (c.moveToNext());

        }
        c.close();

        listAdapter.notifyDataSetChanged();

        SetRescanCount();
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };
}
