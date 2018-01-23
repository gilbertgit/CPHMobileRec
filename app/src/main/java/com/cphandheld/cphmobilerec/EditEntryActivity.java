package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import java.util.Map;

/**
 * Created by titan on 4/13/16.
 */
public class EditEntryActivity extends ActionBarActivity {

    private String TAG = "EditEntryActivity";
    Spinner spinnerNewUsed;
    Spinner spinnerLot;
    Spinner spinnerDealership;
    EditText editTextNotes;

    String sentDealership;
    String sentVin;
    ArrayList<String> sentSelectedItems = new ArrayList<>();

    String newUsedIndex;
    String lotIndex;
    int sentDealershipIndex;
    String newUsed;
    String lot;
    String notes;
    String entryType;
    boolean firstLoad = true;
    boolean isMultiEdit = false;


    DBHelper dbHelper;

    final HashMap<String,String> spinnerDealershipMap = new HashMap<String, String>();
    ArrayList dealershipList = new ArrayList();
    ArrayAdapter<Dealership> dealershipAdapter;
    ArrayAdapter<String> lotAdapter;
    List<String> lotList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        String title = "";
        actionBar.show();

        Intent intent = getIntent();

        dbHelper = new DBHelper(EditEntryActivity.this);
        dbHelper.getWritableDatabase();

        sentVin = intent.getStringExtra("extraVin");
        lotIndex = intent.getStringExtra("extraLotIndex");
        newUsedIndex = intent.getStringExtra("extraNewUsedIndex");
        sentDealershipIndex = intent.getIntExtra("extraDealershipIndex", 0);
        sentSelectedItems = intent.getStringArrayListExtra("extraSelectedItems");

        if(sentSelectedItems.size() > 1) {
            title = "EDITING " + sentSelectedItems.size() + " ITEMS";
            sentDealership = intent.getStringExtra("extraDealership");
            isMultiEdit = true;
        }
        else if (sentSelectedItems.size() == 1){
            sentVin = sentSelectedItems.get(0);
            title = sentVin;
            Physical p = getVehicleEntry(sentVin);
            sentDealership = p.getDealership();
            newUsed = p.getNewUsed();
            lot = p.getLot();
            notes = p.getNotes();
            entryType = p.getEntryType();
        }
        else{
            title = sentVin;
            Physical p = getVehicleEntry(sentVin);
            sentDealership = p.getDealership();
            newUsed = p.getNewUsed();
            lot = p.getLot();
            notes = p.getNotes();
            entryType = p.getEntryType();
        }

        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);

        spinnerDealership = (Spinner) findViewById(R.id.spinnerDealership);
        spinnerNewUsed = (Spinner) findViewById(R.id.spinnerType);
        spinnerLot = (Spinner) findViewById(R.id.spinnerLot);
        editTextNotes = (EditText)findViewById(R.id.textNotes);

        editTextNotes.setText(notes);

        GetDealershipsDB();

        final HashMap<String,String> spinnerNewUsedMap = new HashMap<String, String>();
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

        spinnerNewUsed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                newUsed = spinnerNewUsedMap.get(spinnerNewUsed.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        for (Map.Entry<String, String> e : spinnerNewUsedMap.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();

            if(!isMultiEdit && value.equals(newUsed))
                spinnerNewUsed.setSelection(newUsedAdapter.getPosition(key));
        }

        int p = dealershipAdapter.getPosition(GetDealershipForSpinner());
        spinnerDealership.setSelection(p);

        lotAdapter = new ArrayAdapter<String>(this, R.layout.generic_list, lotList);
        lotAdapter.setDropDownViewResource(R.layout.generic_list);
        spinnerLot.setAdapter(lotAdapter);

        int positionLot = lotAdapter.getPosition(lot);
        spinnerLot.setSelection(positionLot);
        //lot = spinnerLot.getSelectedItem().toString();
        spinnerLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                lot = spinnerLot.getSelectedItem().toString();
                Log.v(TAG, "Lot Selected: " + lot);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
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
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
                SimpleDateFormat tf = new SimpleDateFormat("h:mm:ss aa");
                String formattedDate = df.format(c.getTime());
                String formattedTime = tf.format(c.getTime());

                if(isMultiEdit){
                    for(String v: sentSelectedItems)
                    {
                        DBVehicleEntry.updateVehicleEntry(dbHelper, true, v, sentDealership, newUsed, entryType, lot, formattedDate, formattedTime, editTextNotes.getText().toString().trim(), String.valueOf(Utilities.currentUser.Id));
                    }
                }
                else
                    DBVehicleEntry.updateVehicleEntry(dbHelper, false, sentVin, sentDealership, newUsed, entryType, lot, formattedDate, formattedTime, editTextNotes.getText().toString().trim(), String.valueOf(Utilities.currentUser.Id));

                Intent i = new Intent(EditEntryActivity.this, PhysicalActivity.class);
                setResult(RESULT_OK, i);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("back", true);
                i.putExtra("newUsed", newUsedIndex);
                i.putExtra("lot", lotIndex);
                i.putExtra("dealership", sentDealershipIndex);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(EditEntryActivity.this, PhysicalActivity.class);
        i.putExtra("back", true);
        i.putExtra("newUsed", newUsedIndex);
        i.putExtra("lot", lotIndex);
        i.putExtra("dealership", sentDealershipIndex);
        setResult(RESULT_OK, i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }

    private Physical getVehicleEntry(String vin)
    {
        Physical phy = new Physical();
        Cursor c = DBVehicleEntry.getEntryByVin(dbHelper, vin);

        if (c.moveToFirst()) {
            do {
                int vinIndex = c.getColumnIndex("vin");
                String v = c.getString(vinIndex);

                int dealershipIndex = c.getColumnIndex("dealership");
                String dealership = c.getString(dealershipIndex);

                int newUsedIndex = c.getColumnIndex("newused");
                String newUsed = c.getString(newUsedIndex);

                int entryTypeIndex = c.getColumnIndex("entrytype");
                String entryType = c.getString(entryTypeIndex);

                int lotIndex = c.getColumnIndex("lot");
                String lot = c.getString(lotIndex);

                int dateIndex = c.getColumnIndex("date");
                String date = c.getString(dateIndex);

                int timeIndex = c.getColumnIndex("time");
                String time = c.getString(timeIndex);

                int notesIndex = c.getColumnIndex("notes");
                String notes = c.getString(notesIndex);

                int userIdIndex = c.getColumnIndex("userid");
                String userId = c.getString(userIdIndex);

                int latitudeIndex = c.getColumnIndex("latitude");
                String latitude = c.getString(latitudeIndex);

                int longitudeIndex = c.getColumnIndex("longitude");
                String longitude = c.getString(longitudeIndex);

                phy = new Physical(v, dealership, entryType, newUsed, date, time, lot, notes, userId, latitude, longitude);

            } while (c.moveToNext());
        }
        c.close();
        return phy;
    }

    private Dealership GetDealershipForSpinner()
    {
        Dealership d = new Dealership();
        for(int i = 0; i < dealershipList.size(); i++)
        {
            d = (Dealership)dealershipList.get(i);
            if (d.DealerCode.equals(sentDealership))
                return d;
        }
        return d;
    }

    public void GetDealershipsDB() {

        Cursor c;
        if(DBUsers.hasFilteredDealerships(dbHelper,String.valueOf(Utilities.currentUser.Id)))
            c = DBUsers.getFilteredDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
        else
            c = DBUsers.getDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));

        dealershipList = new ArrayList(c.getCount());
        spinnerDealershipMap.clear();

        if (c.moveToFirst()) {
            do {

                Dealership d = DBUsers.setDealershipData(c);

                if (d.DealerCode.equals(sentDealership)) {
                    if(!d.Lot1Name.equals(""))
                    lotList.add(d.Lot1Name);
                    if(!d.Lot2Name.equals(""))
                    lotList.add(d.Lot2Name);
                    if(!d.Lot3Name.equals(""))
                    lotList.add(d.Lot3Name);
                    if(!d.Lot4Name.equals(""))
                    lotList.add(d.Lot4Name);
                    if(!d.Lot5Name.equals(""))
                    lotList.add(d.Lot5Name);
                    if(!d.Lot6Name.equals(""))
                    lotList.add(d.Lot6Name);
                    if(!d.Lot7Name.equals(""))
                    lotList.add(d.Lot7Name);
                    if(!d.Lot8Name.equals(""))
                    lotList.add(d.Lot8Name);
                    if(!d.Lot9Name.equals(""))
                    lotList.add(d.Lot9Name);
                }

                dealershipList.add(d);

                spinnerDealershipMap.put(d.Name, d.DealerCode);

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

                    // Android is firing this off when the activity loads.. Not sure why so ignore first event
                    if(firstLoad)
                    {
                        firstLoad = false;
                        return;
                    }

                    sentDealership = spinnerDealershipMap.get(spinnerDealership.getSelectedItem().toString());
                    Log.v(TAG, "Dealership Selected: " + sentDealership);

                    lotList.clear();
                    if (!dealershipAdapter.getItem(arg2).Lot1Name.equals(""))
                        lotList.add(dealershipAdapter.getItem(arg2).Lot1Name);
                    if (!dealershipAdapter.getItem(arg2).Lot2Name.equals(""))
                        lotList.add(dealershipAdapter.getItem(arg2).Lot2Name);
                    if (!dealershipAdapter.getItem(arg2).Lot3Name.equals(""))
                        lotList.add(dealershipAdapter.getItem(arg2).Lot3Name);
                    if (!dealershipAdapter.getItem(arg2).Lot4Name.equals(""))
                        lotList.add(dealershipAdapter.getItem(arg2).Lot4Name);
                    if (!dealershipAdapter.getItem(arg2).Lot5Name.equals(""))
                        lotList.add(dealershipAdapter.getItem(arg2).Lot5Name);
                    if (!dealershipAdapter.getItem(arg2).Lot6Name.equals(""))
                        lotList.add(dealershipAdapter.getItem(arg2).Lot6Name);
                    if (!dealershipAdapter.getItem(arg2).Lot7Name.equals(""))
                        lotList.add(dealershipAdapter.getItem(arg2).Lot7Name);
                    if (!dealershipAdapter.getItem(arg2).Lot8Name.equals(""))
                        lotList.add(dealershipAdapter.getItem(arg2).Lot8Name);
                    if (!dealershipAdapter.getItem(arg2).Lot9Name.equals(""))
                        lotList.add(dealershipAdapter.getItem(arg2).Lot9Name);

                    // reset lot spinner
                    lotAdapter.notifyDataSetChanged();
                    spinnerLot.setSelection(0);
                    lot = spinnerLot.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

}
