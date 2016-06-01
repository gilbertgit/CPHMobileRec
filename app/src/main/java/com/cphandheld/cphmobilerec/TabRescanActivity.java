package com.cphandheld.cphmobilerec;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_rescans);

        rescanListView = (ListView)findViewById(R.id.listRescans);

        rescans = new ArrayList();
        listAdapter = new RescanListAdapter(TabRescanActivity.this, 0, rescans, mTouchListener);
        rescanListView.setAdapter(listAdapter);


    }

    @Override
    public void onResume()
    {
        AddRescansToListView();
        IntentFilter filter = new IntentFilter("com.cphandheld.CPHMobileRec.TAB_DATA_REFRESH");
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

    public class UpdaterBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            vinReceived = intent.getStringExtra("vin");
            if(RemoveRescan(vinReceived)) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    private boolean RemoveRescan(String vin)
    {
        for(Rescan r: rescans) {
            if (r.getVIN().equals(vin)) {
                rescans.remove(r);
                return true;
            }
        }
        return false;
    }

    private void AddRescansToListView()
    {
        Rescan rescan = new Rescan("JT3GN86R020257347", "0000A", "", "2014", "Nissan", "Juke", "Gun Metalic", "", "");
        //new Rescan("JT3GN86R020257347", "0000A", "", "2014", "Nissan", "Juke", "Gun Metalic", "05-29-2016 7:20:45", "Gilberto Encarnacion");
        for(int i = 0; i < 10; i++)
        {
            rescans.add(rescan);
        }

        listAdapter.notifyDataSetChanged();
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };



}
