package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
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
import java.lang.reflect.Array;
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
public class PhysicalActivity extends ActionBarActivity implements EMDKListener, AbsListView.OnScrollListener {

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
    ArrayList dealershipList = new ArrayList();
    ArrayAdapter<Dealership> dealershipAdapter;
    ArrayList<String> lotList = new ArrayList<String>();
    ArrayAdapter<String> lotAdapter;

    final HashMap<String, String> spinnerNewUsedMap = new HashMap<String, String>();
    final HashMap<String, String> spinnerDealershipMap = new HashMap<String, String>();

    DBHelper dbHelper;
    RelativeLayout headerLayout;

    private int lastTopValue = 0;

    /////////////////////////////////////////////////////////////

    View mViewToRemove;
    BackgroundContainer mBackgroundContainer;
    boolean mSwiping = false;
    boolean mItemPressed = false;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();


    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;

    //////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>PHYSICAL SCAN</font>"));
        actionBar.show();

        dbHelper = new DBHelper(PhysicalActivity.this);
        dbHelper.getWritableDatabase();

        vehicleList = (ListView) findViewById(R.id.listPhysical);
        vehicleList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.physical_header, vehicleList, false);
        vehicleList.addHeaderView(header, null, false);

        mBackgroundContainer = (BackgroundContainer) findViewById(R.id.listViewBackground);

        phys = new ArrayList();
        listAdapter = new PhysicalListAdapter(PhysicalActivity.this, 0, phys, mTouchListener);
        vehicleList.setAdapter(listAdapter);

        headerLayout = (RelativeLayout) findViewById(R.id.testLayout);

        spinnerDealership = (Spinner) findViewById(R.id.spinnerDealership);
        selectedDealership = "0000C";

        spinnerNewUsed = (Spinner) findViewById(R.id.spinnerType);
        spinnerLot = (Spinner) findViewById(R.id.spinnerLot);
        textCount = (TextView) findViewById(R.id.textCount);

        List<String> listNewUsed = new ArrayList<String>();
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

        //GetDealershipsDB();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Boolean backPress = data.getBooleanExtra("back", false);

                if (backPress) {

                }
            }
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        float mDownX;
        private int mSwipeSlop = -1;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(PhysicalActivity.this).
                        getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1);
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE: {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            vehicleList.requestDisallowInterceptTouchEvent(true);
                            mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                        }
                    }
                    if (mSwiping) {
                        v.setTranslationX((x - mDownX));
                        v.setAlpha(1 - deltaXAbs / v.getWidth());
                    }
                }
                break;
                case MotionEvent.ACTION_UP: {
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        float fractionCovered;
                        float endX;
                        float endAlpha;
                        final boolean remove;
                        if (deltaXAbs > v.getWidth() / 4) {
                            // Greater than a quarter of the width - animate it out
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            endAlpha = 0;
                            remove = true;
                        } else {
                            // Not far enough - animate it back
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            endAlpha = 1;
                            remove = false;
                        }
                        // Animate position and alpha of swiped item
                        // NOTE: This is a simplified version of swipe behavior, for the
                        // purposes of this demo about animation. A real version should use
                        // velocity (via the VelocityTracker class) to send the item off or
                        // back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        vehicleList.setEnabled(false);
                        v.animate().setDuration(duration).
                                alpha(endAlpha).translationX(endX).
                                withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Restore animated values
                                        v.setAlpha(1);
                                        v.setTranslationX(0);
                                        if (remove) {
                                            animateRemoval(vehicleList, v);
                                        } else {
                                            mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            vehicleList.setEnabled(true);
                                        }
                                    }
                                });
                    } else {
                        Utilities.PlayClick(getApplicationContext());
                        int position = vehicleList.getPositionForView(v);

                        Intent i = new Intent(PhysicalActivity.this, EditEntryActivity.class);
                        i.putExtra("extraDealership", listAdapter.getItem(position - 1).getDealership());
                        i.putExtra("extraLot", listAdapter.getItem(position - 1).getLot());
                        i.putExtra("extraNewUsed", listAdapter.getItem(position - 1).getNewUsed());
                        i.putExtra("extraVin", listAdapter.getItem(position - 1).getVIN());
                        i.putExtra("extraEntryType", listAdapter.getItem(position - 1).getEntryType());
                        i.putExtra("extraNotes", listAdapter.getItem(position - 1).getNotes());
                        // i.putExtra("extraDealerPos", dealershipAdapter.getPosition(listAdapter.getItem(position-1)));
                        startActivity(i);
                    }
                }
                mItemPressed = false;
                break;
                default:
                    return false;
            }
            return true;
        }
    };

    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    private void animateRemoval(final ListView listview, View viewToRemove) {
        mViewToRemove = viewToRemove;
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = listAdapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(PhysicalActivity.this);
        builder.setTitle("Remove Entry?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Delete the item from the adapter
                int position = vehicleList.getPositionForView(mViewToRemove);
                DBVehicleEntry.removePhysicalByVin(dbHelper, listAdapter.getItem(position - 1).getVIN());
                listAdapter.remove(listAdapter.getItem(position - 1));
                textCount.setText("Count: " + listAdapter.getCount());
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


        final ViewTreeObserver observer = listview.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listview.getFirstVisiblePosition();
                for (int i = 0; i < listview.getChildCount(); ++i) {
                    final View child = listview.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = listAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        vehicleList.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listview.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mBackgroundContainer.hideBackground();
                                    mSwiping = false;
                                    vehicleList.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }

    public void GetDealershipsDB() {
        Cursor c = DBUsers.getDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
        dealershipList = new ArrayList(c.getCount());
        spinnerDealershipMap.clear();

        if (c.moveToFirst()) {
            do {

                Dealership d = DBUsers.setDealershipData(c);

                if (c.getPosition() == 0) {
                    lotList.add(d.Lot1Name);
                    lotList.add(d.Lot2Name);
                    lotList.add(d.Lot3Name);
                    lotList.add(d.Lot4Name);
                    lotList.add(d.Lot5Name);
                    lotList.add(d.Lot6Name);
                    lotList.add(d.Lot7Name);
                    lotList.add(d.Lot8Name);
                    lotList.add(d.Lot9Name);
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
            spinnerDealership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    selectedDealership = spinnerDealershipMap.get(spinnerDealership.getSelectedItem().toString());
                    //lotList = new ArrayList<String>(9);
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
                    lotAdapter.notifyDataSetChanged();
                    GetPhysicalDB(false);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
            spinnerDealership.setSelection(0);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_physical, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_manual:
                Intent i = new Intent(PhysicalActivity.this, ManualEntryActivity.class);
                i.putExtra("extraDealership", selectedDealership);
                i.putExtra("extraLot", selectedLot);
                i.putExtra("extraNewUsed", selectedValueNewUsed);
                startActivity(i);
                break;
            case R.id.action_upload:
                AlertDialog.Builder builder = new AlertDialog.Builder(PhysicalActivity.this);
                builder.setTitle("Upload Physical Inventory");
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

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
// TODO Auto-generated method stub
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
                    //Get the data from the intent
                    String data = intent.getStringExtra(getString(R.string.datawedge_data_string));

                    //Check that we have received data
                    if (data != null && data.length() > 0) {
                        String barcode = Utilities.CheckVinSpecialCases(data);

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
                        SimpleDateFormat tf = new SimpleDateFormat("h:mm:ss aa");
                        String formattedDate = df.format(c.getTime());
                        String formattedTime = tf.format(c.getTime());

                        if (DBVehicleEntry.isVinScanned(dbHelper, barcode)) {
                            DBVehicleEntry.updateEntryDate(dbHelper, barcode, formattedDate, formattedTime);
                            GetPhysicalDB(true);
                        } else {
                            Animation anim = AnimationUtils.loadAnimation(
                                    PhysicalActivity.this, android.R.anim.slide_in_left
                            );
                            anim.setDuration(500);
                            if(listAdapter.getCount() > 0)
                                vehicleList.getChildAt(1).startAnimation(anim);
                            else
                                vehicleList.getChildAt(0).startAnimation(anim);

                            phys.add(0, new Physical(barcode, selectedDealership, "Scanned", selectedValueNewUsed, formattedDate, formattedTime, selectedLot, "", String.valueOf(Utilities.currentUser.Id)));
                            DBVehicleEntry.insertVehicleEntry(dbHelper, barcode, selectedDealership, selectedValueNewUsed, "Scanned", selectedLot, formattedDate, formattedTime, String.valueOf(Utilities.currentUser.Id));

                            textCount.setText("Count(" + vehicleList.getCount() + ")");
                            vehicleList.smoothScrollToPosition(0);

                            new Handler().postDelayed(new Runnable() {

                                public void run() {


                                    listAdapter.notifyDataSetChanged();

                                }

                            }, anim.getDuration());


                            //vehicleList.setLayoutAnimation();
                        }


                    }
                }
            }
        };
        //Register our receiver.
        this.registerReceiver(EMDKReceiver, intentFilter);
        GetDealershipsDB();

        lotAdapter = new ArrayAdapter<String>(this, R.layout.generic_list, lotList);
        lotAdapter.setDropDownViewResource(R.layout.generic_list);
        spinnerLot.setAdapter(lotAdapter);
        spinnerLot.setSelection(0);
        selectedLot = spinnerLot.getSelectedItem().toString();
        spinnerLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                selectedLot = spinnerLot.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        GetPhysicalDB(true);
    }

    public void GetPhysicalDB(boolean firstLoad) {

        phys = new ArrayList();
        listAdapter = new PhysicalListAdapter(PhysicalActivity.this, 0, phys, mTouchListener);
        vehicleList.setAdapter(listAdapter);

        Cursor c = DBVehicleEntry.getPhysicalByDealership(dbHelper, selectedDealership);

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

                Physical phy = new Physical(vin, dealership, entryType, newUsed, date, time, lot, notes, userId);
                phys.add(phy);
            } while (c.moveToNext());
        }
        c.close();

        if (phys != null && phys.size() > 0) {
            listAdapter.notifyDataSetChanged();

            textCount.setText("Count: " + listAdapter.getCount());
        }
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

    private class UploadTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            inventoryData = "";
            Gson gson = new Gson();
            ArrayList physical = DBVehicleEntry.GetPhysicalForUpload(dbHelper, String.valueOf(Utilities.currentUser.Id));

            if (!physical.equals(null) && physical.size() != 0) {
                inventoryData += "{\"Inventory\":[";
                for (int i = 0; i < physical.size(); i++) {
                    String json = gson.toJson(physical.get(i));
                    inventoryData += json;
                    if(i != physical.size()-1)
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

            if(result)
            {
                dbHelper.ExportDB(getApplicationContext());
                DBVehicleEntry.clearPhysicalTableByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
                phys.clear();
                listAdapter.notifyDataSetChanged();
                textCount.setText("Count(" + vehicleList.getCount() + ")");
            }

//            if (!errorMessage.equals(""))
//                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }

        private boolean UploadInventoryPost() {
            URL url;
            HttpURLConnection connection;
            OutputStreamWriter request;
            InputStreamReader isr;
            JSONObject postData;
            JSONObject responseData;
            String result;

            try {
                if (!inventoryData.equals(null) && inventoryData != "") {


                    String json = inventoryData;
                    String address = Utilities.AppDevURL + Utilities.InventoryUploadURL;
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

                    if (code == 204) {
//                        dbHelper.ExportDB(getApplicationContext());
//                        DBVehicleEntry.clearPhysicalTable(dbHelper);
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
