package com.cphandheld.cphmobilerec;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends ActionBarActivity {

    public static final String PREFS_FILE = "SharedPrefs";
    ImageView imageButton1;
    ImageView imageButton2;
    ImageView imageButton3;
    ImageView imageButton4;
    ImageView imageButton5;
    ImageView imageButton6;
    ImageView imageButton7;
    ImageView imageButton8;
    ImageView imageButton9;
    ImageView imageButton0;
    ImageView imageEntry1;
    ImageView imageEntry2;
    ImageView imageEntry3;
    ImageView imageEntry4;
    ImageView imageEntry5;
    ImageView imageEntry6;
    ImageView imageBack;
    ImageView imageLogo;
    TextView textVersion;

    ProgressDialog mProgressDialog;

    int organizationId = -1;
    String organizationName = "";
    String errorMessage;
    int clickCount = 0;
    boolean isAdmin = false;
    DBHelper dbHelper;
    JSONArray dealershipResults;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(LoginActivity.this);
        dbHelper.getWritableDatabase();

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
        editor = settings.edit();
        organizationId = settings.getInt("orgId", -1);
        organizationName = settings.getString("orgName", "");
        Utilities.AppURL = settings.getString("appURL", "");

        // Get Scanner info
        Utilities.scannerSN = settings.getString("scannerSN", "");
        if(Utilities.scannerSN.equals(""))
        {
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class, String.class );
                Utilities.scannerSN = (String)(   get.invoke(c, "ro.serialno", "unknown" )  );
                editor.putString("scannerSN", Utilities.scannerSN);
            }
            catch (Exception ignored)
            {
            }
        }

        // Get SIM info
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Utilities.simNumber = telemamanger.getSimSerialNumber();
        Utilities.phoneNumber = telemamanger.getLine1Number();
        editor.putString("simNumber", Utilities.simNumber);
        editor.putString("phoneNumber", Utilities.phoneNumber);
        editor.commit();

        String versionName = com.cphandheld.cphmobilerec.BuildConfig.VERSION_NAME;
        Utilities.softwareVersion = versionName;
        textVersion = (TextView) findViewById(R.id.textVersion);
        textVersion.setText(versionName);

        imageEntry1 = (ImageView) findViewById(R.id.entry1);
        imageEntry2 = (ImageView) findViewById(R.id.entry2);
        imageEntry3 = (ImageView) findViewById(R.id.entry3);
        imageEntry4 = (ImageView) findViewById(R.id.entry4);
        imageEntry5 = (ImageView) findViewById(R.id.entry5);
        imageEntry6 = (ImageView) findViewById(R.id.entry6);

        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("Verifying your credentials...");
        mProgressDialog.setMessage("Hold on a sec...");

        setClickEvents();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(Utilities.AppURL.equals(""))
        {
            Utilities.AppURL = getString(R.string.app_url);
            editor.putString("appURL", Utilities.AppURL);
            editor.commit();
        }
    }

    protected void setClickEvents() {
        imageButton1 = (ImageView) findViewById(R.id.button1);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEntry((String) view.getTag());
            }
        });

        imageButton2 = (ImageView) findViewById(R.id.button2);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEntry((String) view.getTag());
            }
        });

        imageButton3 = (ImageView) findViewById(R.id.button3);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEntry((String) view.getTag());
            }
        });

        imageButton4 = (ImageView) findViewById(R.id.button4);
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEntry((String) view.getTag());
            }
        });

        imageButton5 = (ImageView) findViewById(R.id.button5);
        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEntry((String) view.getTag());
            }
        });

        imageButton6 = (ImageView) findViewById(R.id.button6);
        imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEntry((String) view.getTag());
            }
        });

        imageButton7 = (ImageView) findViewById(R.id.button7);
        imageButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEntry((String) view.getTag());
            }
        });

        imageButton8 = (ImageView) findViewById(R.id.button8);
        imageButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEntry((String) view.getTag());
            }
        });

        imageButton9 = (ImageView) findViewById(R.id.button9);
        imageButton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEntry((String) view.getTag());
            }
        });

        imageButton0 = (ImageView) findViewById(R.id.button0);
        imageButton0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEntry((String) view.getTag());
            }
        });

        imageBack = (ImageView) findViewById(R.id.buttonBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEntry();
            }
        });

        imageLogo = (ImageView) findViewById(R.id.logo);
        imageLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoClick();
            }
        });
    }

    protected void deleteEntry() {
        if (imageEntry6.getTag() != null) {
            imageEntry6.setTag(null);
            imageEntry6.setImageResource(R.drawable.no_pin);
        } else if (imageEntry5.getTag() != null) {
            imageEntry5.setTag(null);
            imageEntry5.setImageResource(R.drawable.no_pin);
        } else if (imageEntry4.getTag() != null) {
            imageEntry4.setTag(null);
            imageEntry4.setImageResource(R.drawable.no_pin);
        } else if (imageEntry3.getTag() != null) {
            imageEntry3.setTag(null);
            imageEntry3.setImageResource(R.drawable.no_pin);
        } else if (imageEntry2.getTag() != null) {
            imageEntry2.setTag(null);
            imageEntry2.setImageResource(R.drawable.no_pin);
        } else if (imageEntry1.getTag() != null) {
            imageEntry1.setTag(null);
            imageEntry1.setImageResource(R.drawable.no_pin);
        }
    }


    protected void setEntry(String tag) {
        if (imageEntry1.getTag() == null) {
            imageEntry1.setTag(tag);
            imageEntry1.setImageResource(R.drawable.yes_pin);
        } else if (imageEntry2.getTag() == null) {
            imageEntry2.setTag(tag);
            imageEntry2.setImageResource(R.drawable.yes_pin);
        } else if (imageEntry3.getTag() == null) {
            imageEntry3.setTag(tag);
            imageEntry3.setImageResource(R.drawable.yes_pin);
        } else if (imageEntry4.getTag() == null) {
            imageEntry4.setTag(tag);
            imageEntry4.setImageResource(R.drawable.yes_pin);
        } else if (imageEntry5.getTag() == null) {
            imageEntry5.setTag(tag);
            imageEntry5.setImageResource(R.drawable.yes_pin);
        } else if (imageEntry6.getTag() == null) {
            imageEntry6.setTag(tag);
            imageEntry6.setImageResource(R.drawable.yes_pin);

            String pin = (String) imageEntry1.getTag() + (String) imageEntry2.getTag() + (String) imageEntry3.getTag() + (String) imageEntry4.getTag() + (String) imageEntry5.getTag() + tag;

            if (isAdmin) {
                if (pin.equals(getString(R.string.admin_password))) {
                    Utilities.currentUser = new User();
                    Intent i = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(i);
                    return;
                }
                else
                {
                    YoyoPin();
                    return;
                }
            }
            else
            {
                new checkConnectionTask().execute(pin);
            }
        }
    }

    private class checkConnectionTask extends AsyncTask<String, Void, Boolean> {
        String pin = "";
        @Override
        protected Boolean doInBackground(String... params) {
            pin = params[0];
            return Utilities.hasInternetAccess(LoginActivity.this);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            {
                new LoginTask().execute(pin);
            }
            else
            {
                // Local Authentication
                if (DBUsers.isUserStored(dbHelper, pin)) {
                    getStoredUser(pin);
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    YoyoPin();
                    Toast.makeText(getApplicationContext(), "User not stored.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void getStoredUser(String pin) {
        Cursor c = DBUsers.getUserByPin(dbHelper, Integer.parseInt(pin));

        if (c.moveToFirst()) {
            do {

                int userIdIndex = c.getColumnIndex("id");
                int userId = c.getInt(userIdIndex);

                int firstNameIndex = c.getColumnIndex("firstname");
                String firstName = c.getString(firstNameIndex);

                int lastNameIndex = c.getColumnIndex("lastname");
                String lastName = c.getString(lastNameIndex);


                Utilities.currentUser = new User();
                Utilities.currentUser.Id = userId;
                Utilities.currentUser.FirstName = firstName;
                Utilities.currentUser.LastName = lastName;

            } while (c.moveToNext());
        }
        c.close();
    }

    protected void logoClick() {
        clickCount++;

        if (clickCount == 7) {
            isAdmin = true;

            Toast toast = Toast.makeText(getApplicationContext(), "Admin mode", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 75);
            toast.show();

            imageButton1.setImageResource(R.drawable.btn1_selector_admin);
            imageButton2.setImageResource(R.drawable.btn2_selector_admin);
            imageButton3.setImageResource(R.drawable.btn3_selector_admin);
            imageButton4.setImageResource(R.drawable.btn4_selector_admin);
            imageButton5.setImageResource(R.drawable.btn5_selector_admin);
            imageButton6.setImageResource(R.drawable.btn6_selector_admin);
            imageButton7.setImageResource(R.drawable.btn7_selector_admin);
            imageButton8.setImageResource(R.drawable.btn8_selector_admin);
            imageButton9.setImageResource(R.drawable.btn9_selector_admin);
            imageButton0.setImageResource(R.drawable.btn0_selector_admin);
            imageBack.setImageResource(R.drawable.btn_delete_selector_admin);

        } else if (clickCount == 14) {
            isAdmin = false;

            Toast toast = Toast.makeText(getApplicationContext(), "Exit Admin mode", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 75);
            toast.show();

            imageButton1.setImageResource(R.drawable.btn1_selector);
            imageButton2.setImageResource(R.drawable.btn2_selector);
            imageButton3.setImageResource(R.drawable.btn3_selector);
            imageButton4.setImageResource(R.drawable.btn4_selector);
            imageButton5.setImageResource(R.drawable.btn5_selector);
            imageButton6.setImageResource(R.drawable.btn6_selector);
            imageButton7.setImageResource(R.drawable.btn7_selector);
            imageButton8.setImageResource(R.drawable.btn8_selector);
            imageButton9.setImageResource(R.drawable.btn9_selector);
            imageButton0.setImageResource(R.drawable.btn0_selector);
            imageBack.setImageResource(R.drawable.btn_delete_selector);

            clickCount = 0;
        }
    }

    private void YoyoPin() {
        imageEntry1.setImageResource(R.drawable.pin_x);
        imageEntry2.setImageResource(R.drawable.pin_x);
        imageEntry3.setImageResource(R.drawable.pin_x);
        imageEntry4.setImageResource(R.drawable.pin_x);
        imageEntry5.setImageResource(R.drawable.pin_x);
        imageEntry6.setImageResource(R.drawable.pin_x);

        final Vibrator vibe = (Vibrator) LoginActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(200);


        YoYo.with(Techniques.Shake)
                .duration(1000)
                .playOn(imageEntry1);

        YoYo.with(Techniques.Shake)
                .duration(1000)
                .playOn(imageEntry2);

        YoYo.with(Techniques.Shake)
                .duration(1000)
                .playOn(imageEntry3);

        YoYo.with(Techniques.Shake)
                .duration(1000)
                .playOn(imageEntry4);

        YoYo.with(Techniques.Shake)
                .duration(1000)
                .playOn(imageEntry5);

        YoYo.with(Techniques.Shake)
                .duration(1000)
                .playOn(imageEntry6);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageEntry1.setImageResource(R.drawable.no_pin);
                imageEntry2.setImageResource(R.drawable.no_pin);
                imageEntry3.setImageResource(R.drawable.no_pin);
                imageEntry4.setImageResource(R.drawable.no_pin);
                imageEntry5.setImageResource(R.drawable.no_pin);
                imageEntry6.setImageResource(R.drawable.no_pin);
                imageEntry1.setTag(null);
                imageEntry2.setTag(null);
                imageEntry3.setTag(null);
                imageEntry4.setTag(null);
                imageEntry5.setTag(null);
                imageEntry6.setTag(null);
            }
        }, 1500);
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Utilities.currentUser = null;

            return LoginPost(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {

                // If we got a good result, clear dealerships for the current user
                DBUsers.clearDealerships(dbHelper, String.valueOf(Utilities.currentUser.Id));

                // Put dealership result into object array
                ArrayList<Dealership> array = new Gson().fromJson(dealershipResults.toString(), new TypeToken<List<Dealership>>() {
                }.getType());

                // Insert new dealerships
                for (Dealership d : array) {
                    DBUsers.insertDealership(dbHelper, Utilities.currentUser.Id, d.Id, d.Name, d.DealerCode, d.Lot1Name, d.Lot2Name, d.Lot3Name, d.Lot4Name, d.Lot5Name, d.Lot6Name, d.Lot7Name, d.Lot8Name, d.Lot9Name);
                }

                // Get filtered dealership data
                Cursor c = DBUsers.getFilteredDealershipsByUser(dbHelper, String.valueOf(Utilities.currentUser.Id));
                ArrayList<Dealership> filteredDealerships = DBUsers.setDealershipDataList(c);

                // Clear any filtered dealerships that are not in our main list
                for(Dealership dealership : filteredDealerships)
                {
                    if(!DBUsers.isDealershipStored(dbHelper, dealership.getDealerCode(), String.valueOf(Utilities.currentUser.Id) ))
                    {
                        DBUsers.deleteFilteredDealership(dbHelper, dealership.getDealerCode());
                    }
                }

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }

            if (Utilities.currentUser == null) {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid PIN", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 75);
                toast.show();

                YoyoPin();
            }

            mProgressDialog.dismiss();
        }

        private boolean LoginPost(String pin) {

            URL url;
            HttpURLConnection connection;
            JSONObject responseData;
            InputStreamReader isr;
            String result;

            try {
                String address = Utilities.AppURL + Utilities.LoginURL + pin;
                url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                isr = new InputStreamReader(connection.getInputStream());

                if (connection.getResponseCode() == 200) {
                    isr = new InputStreamReader(connection.getInputStream());
                    result = Utilities.StreamToString(isr);
                    responseData = new JSONObject(result);

                    Utilities.currentUser = new User();
                    Utilities.currentUser.Id = responseData.getInt("Id");
                    Utilities.currentUser.FirstName = responseData.getString("FirstName");
                    Utilities.currentUser.LastName = responseData.getString("LastName");

                    dealershipResults = responseData.getJSONArray("Dealerships");

                    if (!DBUsers.isUserStored(dbHelper, pin)) {
                        DBUsers.insertUser(dbHelper, Utilities.currentUser.Id, Integer.parseInt(pin), responseData.getString("FirstName"), responseData.getString("LastName"));
                    }
                    return true;
                } else {
                    isr = new InputStreamReader(connection.getErrorStream());
                    result = Utilities.StreamToString(isr);
                    responseData = new JSONObject(result);
                    errorMessage = responseData.getString("Message");
                    Log.i("LOGIN ERROR: ", errorMessage);
                    return false;
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }


            return false;
        }
    }

}
