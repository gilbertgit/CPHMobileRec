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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    TextView textOrgName;
    TextView textVersion;

    ProgressDialog mProgressDialog;

    int organizationId = -1;
    String organizationName = "";
    String errorMessage;
    int clickCount = 0;
    boolean isAdmin = false;
    DBHelper dbHelper;
    JSONArray dealershipResults;

    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Utilities.androidId = android_id;

        dbHelper = new DBHelper(LoginActivity.this);
        dbHelper.getWritableDatabase();

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        organizationId = settings.getInt("orgId", -1);
        organizationName = settings.getString("orgName", "");
        editor.putString("androidId", android_id);
        editor.commit();

        String versionName = com.cphandheld.cphmobilerec.BuildConfig.VERSION_NAME;
        textVersion = (TextView) findViewById(R.id.textVersion);
        textVersion.setText(versionName);

        if (!organizationName.equals("")) {
            textOrgName = (TextView) findViewById(R.id.textOrgName);
            textOrgName.setText(organizationName.toUpperCase());
        }

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
                    Intent i = new Intent(LoginActivity.this, OrganizationActivity.class);
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
//                if(pin.equals("222222"))
//                {
//                    Intent i = new Intent(LoginActivity.this, PhysicalActivity.class);
//                    startActivity(i);
//                }

                if (Utilities.isNetworkAvailable(LoginActivity.this)) {
                    // Authenticate the user
                    new LoginTask().execute(Integer.toString(organizationId), pin);
                } else {
                    // Local Authentication
                    if (DBUsers.isUserStored(dbHelper, pin)) {
                        getStoredUser(pin);
                        Intent i = new Intent(LoginActivity.this, PhysicalActivity.class);
                        startActivity(i);
                    } else {
                        YoyoPin();
                        Toast.makeText(getApplicationContext(), "User not stored.", Toast.LENGTH_LONG).show();
                    }
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
        imageEntry1.setImageResource(R.drawable.wrong_pin_x);
        imageEntry2.setImageResource(R.drawable.wrong_pin_x);
        imageEntry3.setImageResource(R.drawable.wrong_pin_x);
        imageEntry4.setImageResource(R.drawable.wrong_pin_x);
        imageEntry5.setImageResource(R.drawable.wrong_pin_x);
        imageEntry6.setImageResource(R.drawable.wrong_pin_x);

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

    private class LoginTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            Utilities.currentUser = null;
            if (LoginPost(Integer.parseInt(params[0]), params[1])) {

                Intent i = new Intent(LoginActivity.this, PhysicalActivity.class);
                startActivity(i);
                return null;

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

            ArrayList<Dealership> array = new Gson().fromJson(dealershipResults.toString(), new TypeToken<List<Dealership>>(){}.getType());

            for(Dealership d : array)
            {
                DBUsers.insertDealership(dbHelper, Utilities.currentUser.Id, d.Id, d.Name, d.DealerCode, d.Lot1Name, d.Lot2Name, d.Lot3Name, d.Lot4Name, d.Lot5Name, d.Lot6Name, d.Lot7Name, d.Lot8Name, d.Lot9Name);
            }

            if (Utilities.currentUser == null) {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid PIN", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 75);
                toast.show();

                YoyoPin();
            }
            mProgressDialog.dismiss();
        }

        private boolean LoginPost(int organizationId, String pin) {

            URL url;
            HttpURLConnection connection;
            OutputStreamWriter request;
            JSONObject responseData;
            JSONObject postData;
            InputStreamReader isr;
            String result;
            Gson gson = new Gson();

            try {
                String address = Utilities.AppDevURL + Utilities.LoginURL + pin;
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
                    Log.i("vehicle check in error", errorMessage);
                    return false;
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }


            return false;
        }
    }

}
