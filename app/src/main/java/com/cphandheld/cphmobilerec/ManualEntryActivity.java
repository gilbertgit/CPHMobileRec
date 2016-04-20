package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by titan on 4/11/16.
 */
public class ManualEntryActivity extends ActionBarActivity {

    Keyboard mKeyboard;
    KeyboardView mKeyboardView;
    Button buttonClearText;
    EditText editTextVin;
    Menu mMenu;
    DBHelper dbHelper;

    String sentDealership;
    String sentLot;
    String sentNewUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>MANUAL ENTRY</font>"));
        actionBar.show();

        Intent intent = getIntent();
        sentDealership = intent.getStringExtra("extraDealership");
        sentLot = intent.getStringExtra("extraLot");
        sentNewUsed = intent.getStringExtra("extraNewUsed");

        dbHelper = new DBHelper(ManualEntryActivity.this);
        dbHelper.getWritableDatabase();

        Utilities.initKeyMap();

        editTextVin = (EditText)findViewById(R.id.textManualVin);
        editTextVin.postDelayed(new Runnable() {
            @Override
            public void run() {
                mKeyboardView.setVisibility(View.VISIBLE);
                mKeyboardView.setEnabled(true);            }
        },200);

        buttonClearText = (Button)findViewById(R.id.buttonClearText);
        buttonClearText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editTextVin.setText("");
            }
            });

        // Create the Keyboard
        mKeyboard= new Keyboard(this,R.xml.cph_keyboard);

        // Lookup the KeyboardView
        mKeyboardView= (KeyboardView)findViewById(R.id.keyboardView);
        // Attach the keyboard to the view
        mKeyboardView.setKeyboard( mKeyboard );

        // Do not show the preview balloons
        //mKeyboardView.setPreviewEnabled(false);

        // Install the key handler
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
    }

    public void onBackPressed()
    {
        Intent i = new Intent(this, PhysicalActivity.class);
        i.putExtra("back", true);
        setResult(RESULT_OK, i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual_entry, menu);
        mMenu = menu;
        EnableDoneAction(false);
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
                DBVehicleEntry.insertVehicleEntry(dbHelper, editTextVin.getText().toString(),sentDealership, sentNewUsed, "Manual", sentLot, formattedDate, formattedTime, String.valueOf(Utilities.currentUser.Id));
                Intent i = new Intent(ManualEntryActivity.this, PhysicalActivity.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void EnableDoneAction(boolean enable)
    {
        if(enable)
        {
            mMenu.findItem(R.id.action_done).setEnabled(enable);
            mMenu.findItem(R.id.action_done).setIcon(R.drawable.ic_done_white_24dp);
        }
        else
        {
            mMenu.findItem(R.id.action_done).setEnabled(enable);
            mMenu.findItem(R.id.action_done).setIcon(R.drawable.ic_done_black_24dp);
        }
    }

    private void CheckVinLength(int length)
    {
        if(length == 6 || length == 8 || length == 17)
            EnableDoneAction(true);
        else
            EnableDoneAction(false);
    }

    public void openKeyboard(View v)
    {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if( v!=null)((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override public void onKey(int primaryCode, int[] keyCodes)
        {
            View focusCurrent = ManualEntryActivity.this.getWindow().getCurrentFocus();
            if( focusCurrent==null || focusCurrent.getClass()!=EditText.class ) return;
            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();

            String c = Utilities.keyCodeMap.get(String.valueOf(primaryCode));
            if(!(c == null)){
                if(c != "Q" && c != "I" && c != "O") {
                    editable.append(c);
                    Utilities.PlayClick(getApplicationContext());
                }
                else
                    Utilities.PlayBadClick(getApplicationContext());
            }
            else{
                switch(primaryCode){
                    case -5:
                        int length = edittext.getText().length();
                        if (length > 0) {
                            edittext.getText().delete(length - 1, length);
                            Utilities.PlayClick(getApplicationContext());
                        }
                }
            }
            CheckVinLength(editable.length());
        }

        @Override public void onPress(int arg0) {
            mKeyboardView.setPreviewEnabled(false);
        }

        @Override public void onRelease(int primaryCode) {
        }

        @Override public void onText(CharSequence text) {
        }

        @Override public void swipeDown() {
        }

        @Override public void swipeLeft() {
        }

        @Override public void swipeRight() {
        }

        @Override public void swipeUp() {
        }
    };
}
