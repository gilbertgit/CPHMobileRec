package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by titan on 4/13/16.
 */
public class EditEntryActivity extends ActionBarActivity {

    Spinner spinnerNewUsed;
    Spinner spinnerLot;
    Spinner spinnerDealership;
    EditText editTextNotes;

    String sentDealership;
    String sentLot;
    String sentNewUsed;
    String sentVin;
    String sentEntryType;
    String sentNotes;
    int sentDealerPos;

    DBHelper dbHelper;

    final HashMap<String,String> spinnerDealershipMap = new HashMap<String, String>();
    ArrayList dealershipList = new ArrayList();
    ArrayAdapter<Dealership> dealershipAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        ActionBar actionBar = getActionBar();
        //actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>EDIT ENTRY</font>"));
        actionBar.show();

        Intent intent = getIntent();
        sentDealership = intent.getStringExtra("extraDealership");
        sentLot = intent.getStringExtra("extraLot");
        sentNewUsed = intent.getStringExtra("extraNewUsed");
        sentVin = intent.getStringExtra("extraVin");
        sentEntryType = intent.getStringExtra("extraEntryType");
        sentNotes = intent.getStringExtra("extraNotes");
        sentDealerPos = intent.getIntExtra("extraDealerPos", 0);

        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>" + sentVin + "</font>"));

        spinnerDealership = (Spinner) findViewById(R.id.spinnerDealership);
        spinnerNewUsed = (Spinner) findViewById(R.id.spinnerType);
        spinnerLot = (Spinner) findViewById(R.id.spinnerLot);
        editTextNotes = (EditText)findViewById(R.id.textNotes);
        editTextNotes.setText(sentNotes);

        dbHelper = new DBHelper(EditEntryActivity.this);
        dbHelper.getWritableDatabase();

        HashMap<String,String> spinnerNewUsedMap = new HashMap<String, String>();
        spinnerNewUsedMap.put("New", "0");
        spinnerNewUsedMap.put("Used", "1");
        spinnerNewUsedMap.put("Loaner", "2");

        List<String> listNewUsed = new ArrayList<String>();
        listNewUsed.add("New");
        listNewUsed.add("Used");
        listNewUsed.add("Loaner");

        ArrayAdapter<String> newUsedAdapter = new ArrayAdapter<String>(this, R.layout.generic_list, listNewUsed);
        newUsedAdapter.setDropDownViewResource(R.layout.generic_list);
        spinnerNewUsed.setAdapter(newUsedAdapter);
        int position = newUsedAdapter.getPosition(sentNewUsed);
        spinnerNewUsed.setSelection(position);
        spinnerNewUsed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                sentNewUsed = spinnerNewUsed.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        List<String> listLot = new ArrayList<String>();
        listLot.add("Lot 1");
        listLot.add("Lot 2");
        listLot.add("Lot 3");


        ArrayAdapter<String> lotAdapter = new ArrayAdapter<String>(this, R.layout.generic_list, listLot);
        lotAdapter.setDropDownViewResource(R.layout.generic_list);
        spinnerLot.setAdapter(lotAdapter);
        int positionLot = lotAdapter.getPosition(sentLot);
        spinnerLot.setSelection(positionLot);
        sentLot = spinnerLot.getSelectedItem().toString();
        spinnerLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                sentLot = spinnerLot.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        GetDealershipsDB();
        spinnerDealership.setSelection(sentDealerPos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual_entry, menu);
//        mMenu = menu;
//        EnableDoneAction(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_done:
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
                SimpleDateFormat tf = new SimpleDateFormat("h:mm:ss aa");
                String formattedDate = df.format(c.getTime());
                String formattedTime = tf.format(c.getTime());
                DBVehicleEntry.updateVehicleEntry(dbHelper, sentVin, sentDealership, sentNewUsed, sentEntryType, sentLot, formattedDate, formattedTime, editTextNotes.getText().toString());
                Intent i = new Intent(EditEntryActivity.this, PhysicalActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(EditEntryActivity.this, PhysicalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void GetDealershipsDB()
    {
        Cursor c = DBUsers.getDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
        dealershipList = new ArrayList(c.getCount());
        spinnerDealershipMap.clear();
        int pos = 0;

        if (c.moveToFirst()) {
            do {

                int dealershipIdIndex = c.getColumnIndex("id");
                int dealershipId = c.getInt(dealershipIdIndex);

                int nameIndex = c.getColumnIndex("name");
                String dealershipName = c.getString(nameIndex);

                int dealerCodeIndex = c.getColumnIndex("dealercode");
                String dealerCode = c.getString(dealerCodeIndex);

                int lot1NameIndex = c.getColumnIndex("lot1name");
                String lot1Name = c.getString(lot1NameIndex);

                int lot2NameIndex = c.getColumnIndex("lot2name");
                String lot2Name = c.getString(lot2NameIndex);

                int lot3NameIndex = c.getColumnIndex("lot3name");
                String lot3Name = c.getString(lot3NameIndex);

                int lot4NameIndex = c.getColumnIndex("lot4name");
                String lot4Name = c.getString(lot4NameIndex);

                int lot5NameIndex = c.getColumnIndex("lot5name");
                String lot5Name = c.getString(lot5NameIndex);

                int lot6NameIndex = c.getColumnIndex("lot6name");
                String lot6Name = c.getString(lot6NameIndex);

                int lot7NameIndex = c.getColumnIndex("lot7name");
                String lot7Name = c.getString(lot7NameIndex);

                int lot8NameIndex = c.getColumnIndex("lot8name");
                String lot8Name = c.getString(lot8NameIndex);

                int lot9NameIndex = c.getColumnIndex("lot9name");
                String lot9Name = c.getString(lot9NameIndex);

                Dealership dealership = new Dealership();
                dealership.Id = dealershipId;
                dealership.Name = dealershipName;
                dealership.DealerCode = dealerCode;
                dealership.Lot1Name = lot1Name;
                dealership.Lot2Name = lot2Name;
                dealership.Lot3Name = lot3Name;
                dealership.Lot4Name = lot4Name;
                dealership.Lot5Name = lot5Name;
                dealership.Lot6Name = lot6Name;
                dealership.Lot7Name = lot7Name;
                dealership.Lot8Name = lot8Name;
                dealership.Lot9Name = lot9Name;

                dealershipList.add(dealership);

                spinnerDealershipMap.put(dealershipName, dealerCode);

                if(sentDealership.equals(dealerCode))
                    sentDealerPos = c.getPosition();

            } while (c.moveToNext());
        }
        c.close();

        if (dealershipList != null && dealershipList.size() > 0) {

            dealershipAdapter = new ArrayAdapter<Dealership>(this, R.layout.generic_list, dealershipList);
            dealershipAdapter.setDropDownViewResource(R.layout.generic_list);
            spinnerDealership.setAdapter(dealershipAdapter);

            spinnerDealership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    sentDealership = spinnerDealershipMap.get(spinnerDealership.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

}
