package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by titan on 5/20/16.
 */
public class AdminActivity extends Activity {

    private EditText editTextUrl;
    private Button buttonSetUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        editTextUrl = (EditText)findViewById(R.id.editTextUrl);
        editTextUrl.setText(Utilities.AppURL);
        buttonSetUrl = (Button)findViewById(R.id.buttonSetUrl);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Admin Mode");

        SharedPreferences settings = getSharedPreferences(Utilities.PREFS_FILE, 0);
        final SharedPreferences.Editor editor = settings.edit();

        buttonSetUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.AppURL = editTextUrl.getText().toString();
                editor.putString("appURL", editTextUrl.getText().toString());
                editor.commit();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Toast.makeText(getApplicationContext(), "App Url Changed!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
