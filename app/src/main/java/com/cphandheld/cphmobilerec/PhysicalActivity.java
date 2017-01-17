package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
//import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.splunk.mint.Mint;
import com.splunk.mint.MintLogLevel;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * Created by titan on 4/8/16.
 */
public class PhysicalActivity extends Activity implements EMDKListener, AbsListView.OnScrollListener {

    private String TAG = "PhysicalActivity";

    Spinner spinnerNewUsed;
    Spinner spinnerLot;
    Spinner spinnerDealership;
    TextView textCount;
    String errorMessage;


    ListView vehicleList;
    ArrayList phys;
    private String profileName = "CPHMobile";
    PhysicalListAdapter listAdapter;

    //Declare a variable to store ProfileManager object
    private ProfileManager mProfileManager = null;
    private EMDKManager emdkManager = null;

    private BroadcastReceiver EMDKReceiver;

    String selectedDealership;
    String selectedNewUsed;
    String selectedValueNewUsed;
    String selectedLot;
    String inventoryData;
    int dealershipSelection = 0;
    String lotSelection = "";
    ArrayList dealershipList = new ArrayList();
    ArrayAdapter<Dealership> dealershipAdapter;
    ArrayList<String> lotList = new ArrayList<String>();
    ArrayAdapter<String> lotAdapter;

    final HashMap<String, String> spinnerNewUsedMap = new HashMap<String, String>();
    final HashMap<String, String> spinnerDealershipMap = new HashMap<String, String>();
    List<String> listNewUsed = new ArrayList<String>();

    DBHelper dbHelper;
    RelativeLayout headerLayout;
    ProgressDialog mProgressDialog;

    private int lastTopValue = 0;

    /////////////////////////////////////////////////////////////

    View mViewToRemove;
    BackgroundContainer mBackgroundContainer;
    boolean mSwiping = false;
    boolean mItemPressed = false;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;
    private Vibrator vib;
    private MediaPlayer mp;

    //////////////////////////////////////////////////////////////

    private GPSHelper gpsHelper;
    Intent gpsServiceIntent;
    private Location lastKnownLoc;
    private String latitude;
    private String longitude;

    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>PHYSICAL SCAN</font>"));
        actionBar.show();

        mProgressDialog = new ProgressDialog(PhysicalActivity.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("Uploading Inventory...");
        mProgressDialog.setMessage("Hold on a sec...");

        dbHelper = new DBHelper(PhysicalActivity.this);
        dbHelper.getWritableDatabase();

        vehicleList = (ListView) findViewById(R.id.listPhysical);
        vehicleList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.physical_header, vehicleList, false);
        vehicleList.addHeaderView(header, null, false);

        mBackgroundContainer = (BackgroundContainer) findViewById(R.id.listViewBackground);

        phys = new ArrayList();
        listAdapter = new PhysicalListAdapter(PhysicalActivity.this, 0, phys, mClickListener, mLongClickListener);
        vehicleList.setAdapter(listAdapter);

        headerLayout = (RelativeLayout) findViewById(R.id.testLayout);

        spinnerDealership = (Spinner) findViewById(R.id.spinnerDealership);
        selectedDealership = "";

        spinnerNewUsed = (Spinner) findViewById(R.id.spinnerType);
        spinnerLot = (Spinner) findViewById(R.id.spinnerLot);
        textCount = (TextView) findViewById(R.id.textCount);

        listNewUsed.add("New");
        listNewUsed.add("Used");
        listNewUsed.add("Loaner");

        spinnerNewUsedMap.put("New", "0");
        spinnerNewUsedMap.put("Used", "1");
        spinnerNewUsedMap.put("Loaner", "2");

        ArrayAdapter<String> newUsedAdapter = new ArrayAdapter<String>(this, R.layout.generic_list, listNewUsed);
        newUsedAdapter.setDropDownViewResource(R.layout.generic_list);
        spinnerNewUsed.setAdapter(newUsedAdapter);
        spinnerNewUsed.setSelection(0);
        selectedValueNewUsed = spinnerNewUsedMap.get(spinnerNewUsed.getSelectedItem().toString());
        selectedNewUsed = spinnerNewUsed.getSelectedItem().toString();
        spinnerNewUsed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                selectedValueNewUsed = spinnerNewUsedMap.get(spinnerNewUsed.getSelectedItem().toString());
                selectedNewUsed = spinnerNewUsed.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

        // Create instance of service so we have context
        gpsHelper = new GPSHelper(PhysicalActivity.this);

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt("SELECTED_DEALER", dealershipSelection);
        savedInstanceState.putString("SELECTED_LOT", selectedLot);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        dealershipSelection = savedInstanceState.getInt("SELECTED_DEALER");
        spinnerDealership.setSelection(dealershipSelection);

        lotSelection = savedInstanceState.getString("SELECTED_LOT");
        spinnerLot.setSelection(lotList.indexOf(lotSelection), true);
        Log.v(TAG, "onRestoreInstanceState");
    }

    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                Boolean backPress = data.getBooleanExtra("back", false);
                if (backPress) {
                    dealershipSelection = data.getIntExtra("dealership", 0);
                    String newUsed = data.getStringExtra("newUsed");
                    lotSelection = data.getStringExtra("lot");
                    spinnerNewUsed.setSelection(listNewUsed.indexOf(newUsed));
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");

        gpsHelper.stopUsingGPS();

        stopService(gpsServiceIntent);

        if (emdkManager != null)
            this.emdkManager.release();

        super.onDestroy();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = vehicleList.getPositionForView(v);

            // if we have nothing selected, perform click and go to Edit Entry Activity
            if(mActionMode == null) {

                ArrayList<String> selectedItems = new ArrayList<String>();
                selectedItems.add(listAdapter.getItem(position - 1).getVIN());
                Intent i = new Intent(PhysicalActivity.this, EditEntryActivity.class);;

                i.putExtra("extraSelectedItems", selectedItems);
                i.putExtra("extraLotIndex", selectedLot);
                i.putExtra("extraNewUsedIndex", selectedValueNewUsed);
                i.putExtra("extraDealershipIndex", spinnerDealership.getSelectedItemPosition());
                startActivityForResult(i, 2);
            }
            else
            {
                // else add item clicked to selected list list
                onListItemSelect(position -1);
            }
        }
    };


    private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            int position = vehicleList.getPositionForView(v);
            vehicleList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

            onListItemSelect(position -1);
            return true;
        }
    };

    private void onListItemSelect(int position) {
        listAdapter.toggleSelection(position);
        boolean hasCheckedItems = listAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = startActionMode(new ActionModeCallback());
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(listAdapter
                    .getSelectedCount()) + " selected");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_physical, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_upload:

                // Backup the data before we do anything
                DBVehicleEntry.BackupPhysicalDB(dbHelper, getApplicationContext(), String.valueOf(Utilities.currentUser.Id));

                new ccUploadTask().execute();
                break;
            case R.id.action_manual:

                if(selectedDealership.equals("-1"))
                {
                    Toast.makeText(PhysicalActivity.this, "Please selected a dealership", Toast.LENGTH_LONG).show();
                    Utilities.playError(PhysicalActivity.this);
                    break;
                }

                Intent i = new Intent(PhysicalActivity.this, ManualEntryActivity.class);
                i.putExtra("extraAppType", "PHYSICAL");
                i.putExtra("extraDealership", selectedDealership);
                i.putExtra("extraDealershipIndex", spinnerDealership.getSelectedItemPosition());
                i.putExtra("extraLot", selectedLot);
                i.putExtra("extraNewUsed", selectedValueNewUsed);
                startActivityForResult(i, 2);
                break;
        }

        return true;
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.context_physical, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            final ActionMode mMode = mode;
            switch (item.getItemId()) {
                case R.id.action_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhysicalActivity.this);
                    builder.setTitle("Remove selected entries?");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // retrieve selected items and delete them out
                            SparseBooleanArray selected = listAdapter.getSelectedIds();

                            for (int i = (selected.size()); i >= 0; i--) {
                                if (selected.valueAt(i)) {
                                    int found = selected.keyAt(i);
                                    Physical selectedItem = listAdapter.getItem(found);
                                    DBVehicleEntry.removePhysicalByVin(dbHelper, listAdapter.getItem(found).getVIN());
                                    listAdapter.remove(selectedItem);
                                    textCount.setText("Count( " + phys.size() + ")");
                                }
                            }
                            mMode.finish(); // Action picked, so close the CAB
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //TODO
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                case R.id.action_edit:

                    SparseBooleanArray selected = listAdapter.getSelectedIds();
                    ArrayList<String> selectedItems = new ArrayList<String>();

                    for (int i = (selected.size()); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            int found = selected.keyAt(i);
                            Physical selectedItem = listAdapter.getItem(found);
                            selectedItems.add(selectedItem.getVIN());
                        }
                    }

                    Intent i = new Intent(PhysicalActivity.this, EditEntryActivity.class);

                    if(selectedItems.size() > 1)
                        i.putExtra("extraDealership", selectedDealership);

                    i.putExtra("extraSelectedItems", selectedItems);
                    i.putExtra("extraLotIndex", selectedLot);
                    i.putExtra("extraNewUsedIndex", selectedValueNewUsed);
                    i.putExtra("extraDealershipIndex", spinnerDealership.getSelectedItemPosition());
                    startActivityForResult(i, 2);

                    mMode.finish();

                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // remove selection
            listAdapter.removeSelection();
            mActionMode = null;
        }
    }

    public void GetDealershipsDB() {

        Dealership initialDealership = new Dealership();
        initialDealership.Id = -1;
        initialDealership.DealerCode = "-1";
        initialDealership.Name = "(Select Dealership)";
        initialDealership.Lot1Name = "*";
        initialDealership.Lot2Name = "*";
        initialDealership.Lot3Name = "*";
        initialDealership.Lot4Name = "*";
        initialDealership.Lot5Name = "*";
        initialDealership.Lot6Name = "*";
        initialDealership.Lot7Name = "*";
        initialDealership.Lot8Name = "*";
        initialDealership.Lot9Name = "*";


        Cursor c;
        if (DBUsers.hasFilteredDealerships(dbHelper, String.valueOf(Utilities.currentUser.Id)))
            c = DBUsers.getFilteredDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
        else
            c = DBUsers.getDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));

        dealershipList = new ArrayList(c.getCount());
        dealershipList.add(initialDealership);
        spinnerDealershipMap.clear();
        spinnerDealershipMap.put(initialDealership.Name, initialDealership.DealerCode);

        if (c.moveToFirst()) {
            do {

                Dealership d = DBUsers.setDealershipData(c);

                int pos = 0;
                if (dealershipSelection != 0)
                    pos = dealershipSelection;

                if (c.getPosition() == pos) {
                    lotList.add(d.Lot1Name);
                    lotList.add(d.Lot2Name);
                    lotList.add(d.Lot3Name);
                    lotList.add(d.Lot4Name);
                    lotList.add(d.Lot5Name);
                    lotList.add(d.Lot6Name);
                    lotList.add(d.Lot7Name);
                    lotList.add(d.Lot8Name);
                    lotList.add(d.Lot9Name);

                    lotAdapter = new ArrayAdapter<>(this, R.layout.generic_list, lotList);
                    lotAdapter.setDropDownViewResource(R.layout.generic_list);
                    spinnerLot.setAdapter(lotAdapter);

                    if (lotSelection.equals(""))
                        spinnerLot.setSelection(0);
                    else
                        spinnerLot.setSelection(lotList.indexOf(lotSelection), true);

                    selectedLot = spinnerLot.getSelectedItem().toString();
                }

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
                spinnerDealership.setSelection(0, true);
            else {
                spinnerDealership.setSelection(dealershipSelection, true);
            }

            spinnerDealership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    selectedDealership = spinnerDealershipMap.get(spinnerDealership.getSelectedItem().toString());
                    dealershipSelection = spinnerDealership.getSelectedItemPosition();

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

                    // reset the lot spinner
                    lotAdapter.notifyDataSetChanged();
                    spinnerLot.setSelection(0);
                    selectedLot = spinnerLot.getSelectedItem().toString();
                    GetPhysicalDB();
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });

            selectedDealership = spinnerDealershipMap.get(spinnerDealership.getSelectedItem().toString());
            GetPhysicalDB();
        }
    }

    @Override
    protected void onResume() {
// TODO Auto-generated method stub
        Log.v(TAG, "onResume");
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(getString(R.string.scan_intent));
        //Create a our Broadcast Receiver.
        EMDKReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Get the source of the data

                String source = intent.getStringExtra(getString(R.string.datawedge_source));

                //Check if the data has come from the barcode scanner
                if (source.equalsIgnoreCase("scanner")) {

                    if(selectedDealership.equals("-1"))
                    {
                        Toast.makeText(PhysicalActivity.this, "Please selected a dealership", Toast.LENGTH_LONG).show();
                        Utilities.playError(PhysicalActivity.this);
                        return;
                    }
                    //Get the data from the intent
                    String data = intent.getStringExtra(getString(R.string.datawedge_data_string));

                    //Check that we have received data
                    if (data != null && data.length() > 0) {
                        String barcode = Utilities.CheckVinSpecialCases(data);

                        if (!Utilities.isValidVin(barcode)) {
                            // alert user that vin is not valid
                           Utilities.playError(PhysicalActivity.this);

                        }

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
                        SimpleDateFormat tf = new SimpleDateFormat("h:mm:ss aa");
                        String formattedDate = df.format(c.getTime());
                        String formattedTime = tf.format(c.getTime());

                        Log.v(TAG, "Position: " + latitude + "," + longitude);

                        if (DBVehicleEntry.isVinScanned(dbHelper, barcode)) {
                            DBVehicleEntry.updateEntry(dbHelper, barcode, formattedDate, formattedTime, latitude, longitude);
                            GetPhysicalDB();
                        } else {
                            Animation anim = AnimationUtils.loadAnimation(
                                    PhysicalActivity.this, android.R.anim.slide_in_left
                            );
                            anim.setDuration(500);

                            int count = listAdapter.getCount();
                            if (count > 0)
                                vehicleList.getChildAt(1).startAnimation(anim);
                            else
                                vehicleList.getChildAt(0).startAnimation(anim);

                            phys.add(0, new Physical(barcode, selectedDealership, "Scanned", selectedValueNewUsed, formattedDate, formattedTime, selectedLot, "", String.valueOf(Utilities.currentUser.Id), latitude, longitude));

                            DBVehicleEntry.insertVehicleEntry(dbHelper, barcode, selectedDealership, selectedValueNewUsed, "Scanned", selectedLot, formattedDate, formattedTime, String.valueOf(Utilities.currentUser.Id), latitude, longitude);

                            textCount.setText("Count(" + phys.size() + ")");
                            vehicleList.smoothScrollToPosition(0);

                            new Handler().postDelayed(new Runnable() {

                                public void run() {
                                    listAdapter.notifyDataSetChanged();

                                }

                            }, anim.getDuration());

                        }


                    }
                }
            }
        };
        //Register our receiver.
        this.registerReceiver(EMDKReceiver, intentFilter);

        lotList.clear();
        GetDealershipsDB();


        spinnerLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                selectedLot = spinnerLot.getSelectedItem().toString();
                lotSelection = spinnerLot.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        GetPhysicalDB();
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

    public void GetPhysicalDB() {

        phys = new ArrayList();
        listAdapter = new PhysicalListAdapter(PhysicalActivity.this, 0, phys, mClickListener, mLongClickListener);
        vehicleList.setAdapter(listAdapter);

        Cursor c = DBVehicleEntry.getPhysicalByDealership(PhysicalActivity.this, dbHelper, selectedDealership);

        if (c.moveToFirst()) {
            do {
                int vinIndex = c.getColumnIndex("vin");
                String vin = c.getString(vinIndex);

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

                Physical phy = new Physical(vin, dealership, entryType, newUsed, date, time, lot, notes, userId, latitude, longitude);
                phys.add(phy);
            } while (c.moveToNext());
        }
        c.close();

        if (phys != null && phys.size() > 0) {
            listAdapter.notifyDataSetChanged();
        }

        textCount.setText("Count(" + phys.size() + ")");
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
                }
            } catch (Exception ex) {
                // Handle any exception
            }
        }
    }

    @Override
    public void onClosed() {

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Kill the GPS receiver
        //gpsHelper.stopUsingGPS();
        //Register our receiver.
        this.unregisterReceiver(this.EMDKReceiver);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Rect rect = new Rect();
        headerLayout.getLocalVisibleRect(rect);
        // backgroundImage.getLocalVisibleRect(rect);
        if (lastTopValue != rect.top) {
            lastTopValue = rect.top;
            headerLayout.setY((float) (rect.top / 2.0));
        }

    }

    private void DisplayResults(JSONObject data) {

        JSONObject responseData;
        JSONArray uploadResults;
        String dialogText = "";
        int totalCount;

        try {
            responseData = (data);
            totalCount = responseData.getInt("TotalCount");
            uploadResults = responseData.getJSONArray("UploadResult");

            for (int i = 0; i < uploadResults.length(); i++) {
                JSONObject object = uploadResults.getJSONObject(i);
                dialogText += "<div><b>" + object.getString("Dealership") + " - " + object.getString("DealerCode") + "</b><br>";
                dialogText += "Inventory Count: " + object.getString("InventoryCount") + "<br>";
                dialogText += "Bad VIN Count: " + object.getString("BadVinCount") + "</div>";

            }

            AlertDialog alertDialog = new AlertDialog.Builder(PhysicalActivity.this).create();
            alertDialog.setTitle("Total Inventory Uploaded: " + totalCount);
            alertDialog.setMessage(Html.fromHtml(dialogText));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

//            HashMap<String, Object> myData = new HashMap<String, Object>();
//            myData.put("User", Utilities.currentUser.Id);
//            myData.put("Scanner SN", Utilities.scannerSN);
//            myData.put("SIM", Utilities.simNumber);
//            myData.put("Phone", Utilities.phoneNumber);
//            myData.put("Version", Utilities.softwareVersion);
//            myData.put("Upload Date", Utilities.getSimpleDateTime());
//            myData.put("Total Count", totalCount);
//            Mint.logEvent("Upload Complete", MintLogLevel.Info, myData);

        } catch (JSONException ex) {
        }
    }

    private class ccUploadTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            return Utilities.hasInternetAccess(PhysicalActivity.this);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhysicalActivity.this);
                builder.setTitle("Upload Physical Inventory?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new UploadTask().execute();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        dialog.dismiss();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private class UploadTask extends AsyncTask<String, Void, Boolean> {

        JSONObject responseData = new JSONObject();

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();

            inventoryData = "";
            Gson gson = new Gson();
            ArrayList physical = DBVehicleEntry.GetPhysicalForUpload(dbHelper, String.valueOf(Utilities.currentUser.Id));

            if (!physical.equals(null) && physical.size() != 0) {
                inventoryData += "{\"ScannerUserId\":\"" + Utilities.currentUser.Id + "\",\"SoftwareVersion\":\"" + Utilities.softwareVersion + "\",\"ScannerSerialNumber\":\"" + Utilities.scannerSN + "\",\"Inventory\":[";
                for (int i = 0; i < physical.size(); i++) {
                    String json = gson.toJson(physical.get(i));
                    inventoryData += json;
                    if (i != physical.size() - 1)
                        inventoryData += ",";
                }
                inventoryData += "]}";
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            errorMessage = "";

            return UploadInventoryPost();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {

                DBVehicleEntry.clearPhysicalTableByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
                phys.clear();
                listAdapter.notifyDataSetChanged();
                textCount.setText("Count(" + phys.size() + ")");

                mProgressDialog.dismiss();

                DisplayResults(responseData);

            } else {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }

        private boolean UploadInventoryPost() {
            URL url;
            HttpURLConnection connection;
            OutputStreamWriter request;
            InputStreamReader isr;

            String result;

            try {
                if (!inventoryData.equals(null) && inventoryData != "") {


                    String json = inventoryData;
                    String address = Utilities.AppURL + Utilities.InventoryUploadURL;
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
                        responseData = new JSONObject(result);
                        //inventoryCount = responseData.getInt("InventoryCount");
                        //duplicateCount = responseData.getInt("DuplicateCount");
                        //badVinCount = responseData.getInt("BadVinCount");
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
}
