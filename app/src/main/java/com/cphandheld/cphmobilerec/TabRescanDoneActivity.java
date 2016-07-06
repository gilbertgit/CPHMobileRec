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
    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.intent_rescan_sync));
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

    private class UpdaterBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "UpdaterBroadcastReceiver");
            String action = intent.getAction();
            if (action.equals(getString(R.string.intent_rescan_sync))) {
                completedRescans.clear();
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    private void AddRescansToListView() {
        completedRescans.clear();
        Cursor c = DBRescan.getCompletedRescans(dbHelper, "0000A");

        if (c.moveToFirst()) {
            do {

                Rescan r = DBRescan.setRescanData(c);
                completedRescans.add(r);
            } while (c.moveToNext());

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
