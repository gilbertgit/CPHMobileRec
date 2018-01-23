package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.splunk.mint.Mint;

import java.util.ArrayList;

/**
 * Created by titan on 10/6/16.
 */
public class FilterDealershipActivity extends AppCompatActivity {

    private String TAG = "FilterDealership";

    DBHelper dbHelper;
    ListView listAllDealerships;
    ListView listFilteredDealerships;

    ArrayList allDealershipsList;
    ArrayList filteredDealershipsList;
    DealershipListViewAdapter allDealershipsAdapter;
    DealershipListViewAdapter filteredDealershipsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_dealerships);
        Mint.leaveBreadcrumb("FilterDealershipActivity-onCreate");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        dbHelper = new DBHelper(FilterDealershipActivity.this);
        dbHelper.getWritableDatabase();

        listAllDealerships = (ListView) findViewById(R.id.listAllDealerships);
        listAllDealerships.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listFilteredDealerships = (ListView) findViewById(R.id.listFilteredDealerships);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        Mint.leaveBreadcrumb("FilterDealershipActivity-onResume");

        filteredDealershipsList = new ArrayList();
        filteredDealershipsAdapter = new DealershipListViewAdapter(FilterDealershipActivity.this, 0, filteredDealershipsList, mFilteredDealershipsTouchListener);
        listFilteredDealerships.setAdapter(filteredDealershipsAdapter);

        GetDealershipsDB();
        GetFilteredDealershipsDB();

    }

    private View.OnClickListener mAllDealershipsTouchListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            int position = listAllDealerships.getPositionForView(v);
            Dealership d = (Dealership)allDealershipsAdapter.getItem(position);

            boolean exists = false;
            for(int i = 0; i < filteredDealershipsAdapter.getCount(); i++)
            {
                String code = ((Dealership)filteredDealershipsAdapter.getItem(i)).getDealerCode();
                if(code.equals(d.getDealerCode())) {
                    exists = true;
                    return;
                }
            }

            if(!exists) {
                DBUsers.insertSelectedDealership(dbHelper, Utilities.currentUser.Id, d.Id, d.Name, d.DealerCode, d.Lot1Name, d.Lot2Name, d.Lot3Name, d.Lot4Name, d.Lot5Name, d.Lot6Name, d.Lot7Name, d.Lot8Name, d.Lot9Name);

                filteredDealershipsList.add(d);

                filteredDealershipsAdapter.notifyDataSetChanged();
            }
        }
    };

    private View.OnClickListener mFilteredDealershipsTouchListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = listFilteredDealerships.getPositionForView(v);
            Dealership d = (Dealership)filteredDealershipsAdapter.getItem(position);

            DBUsers.deleteFilteredDealership(dbHelper, d.getDealerCode());

            filteredDealershipsList.remove(d);

            filteredDealershipsAdapter.notifyDataSetChanged();
        }
    };


    private void GetDealershipsDB() {

        Cursor c = DBUsers.getDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
        allDealershipsList = new ArrayList(c.getCount());
        allDealershipsAdapter = new DealershipListViewAdapter(FilterDealershipActivity.this, 0, allDealershipsList, mAllDealershipsTouchListener);
        listAllDealerships.setAdapter(allDealershipsAdapter);

        if (c.moveToFirst()) {
            do {

                Dealership d = DBUsers.setDealershipData(c);

                allDealershipsList.add(d);

            } while (c.moveToNext());
        }
        c.close();

        if (allDealershipsList != null && allDealershipsList.size() > 0) {
            allDealershipsAdapter.notifyDataSetChanged();
        }

    }

    private void GetFilteredDealershipsDB() {

        Cursor c = DBUsers.getFilteredDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
        filteredDealershipsList = new ArrayList(c.getCount());
        filteredDealershipsAdapter = new DealershipListViewAdapter(FilterDealershipActivity.this, 0, filteredDealershipsList, mFilteredDealershipsTouchListener);
        listFilteredDealerships.setAdapter(filteredDealershipsAdapter);

        if (c.moveToFirst()) {
            do {

                Dealership d = DBUsers.setDealershipData(c);

                filteredDealershipsList.add(d);

            } while (c.moveToNext());
        }
        c.close();

        if (filteredDealershipsList != null && filteredDealershipsList.size() > 0) {
            filteredDealershipsAdapter.notifyDataSetChanged();
        }

    }

}
