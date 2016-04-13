package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    DBHelper dbHelper;

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
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>" + sentVin + "</font>"));

        spinnerDealership = (Spinner) findViewById(R.id.spinnerDealership);
        spinnerNewUsed = (Spinner) findViewById(R.id.spinnerType);
        spinnerLot = (Spinner) findViewById(R.id.spinnerLot);
        editTextNotes = (EditText)findViewById(R.id.textNotes);

        dbHelper = new DBHelper(EditEntryActivity.this);
        dbHelper.getWritableDatabase();

        List<String> listNewUsed = new ArrayList<String>();
        listNewUsed.add("New");
        listNewUsed.add("Used");
        listNewUsed.add("Loaner");

        ArrayAdapter<String> newUsedAdapter = new ArrayAdapter<String>(this, R.layout.generic_list, listNewUsed);
        newUsedAdapter.setDropDownViewResource(R.layout.generic_list);
        spinnerNewUsed.setAdapter(newUsedAdapter);
        int position = newUsedAdapter.getPosition(sentNewUsed);
        spinnerNewUsed.setSelection(position);
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
                DBVehicleEntry.insertVehicleEntry(dbHelper, sentVin,sentDealership, sentNewUsed, "Manual", sentLot, formattedDate, formattedTime );
                Intent i = new Intent(EditEntryActivity.this, PhysicalActivity.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
