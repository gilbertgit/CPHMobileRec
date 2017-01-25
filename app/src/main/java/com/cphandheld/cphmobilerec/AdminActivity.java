package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * Created by titan on 5/20/16.
 */
public class AdminActivity extends Activity {

    EditText editTextUrl;
    Button buttonSetUrl;
    Button buttonExportData;
    Button buttonClearDB;
    Button buttonUploadData;

    DBHelper dbHelper;

    static final String FTP_HOST= "ftp.cphandheld.com";
    static final String FTP_USER = "scannerftp";
    static final String FTP_PASS  ="7ygvfr4*";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dbHelper = new DBHelper(AdminActivity.this);
        dbHelper.getWritableDatabase();

        editTextUrl = (EditText)findViewById(R.id.editTextUrl);
        editTextUrl.setText(Utilities.AppURL);
        buttonSetUrl = (Button)findViewById(R.id.buttonSetUrl);
        buttonExportData = (Button)findViewById(R.id.buttonExportData);
        buttonUploadData = (Button)findViewById(R.id.buttonUploadData);
        buttonClearDB = (Button)findViewById(R.id.buttonClearDB);

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

        buttonExportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Back it up
                File physicalFile = DBVehicleEntry.BackupPhysicalDBAdmin(dbHelper);
                File rescanFile = DBRescan.BackupRescanDBAdmin(dbHelper);


                uploadFile(physicalFile);
                if(physicalFile !=  null && rescanFile != null)
                    Toast.makeText(AdminActivity.this, "Successful backup", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AdminActivity.this, "Backup error occurred", Toast.LENGTH_LONG).show();
            }
        });

        buttonUploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Back it up
                File physicalFile = DBVehicleEntry.BackupPhysicalDBAdmin(dbHelper);
                File rescanFile = DBRescan.BackupRescanDBAdmin(dbHelper);

                if(physicalFile !=  null && rescanFile != null)
                    ShowBackupDialog(physicalFile, rescanFile);
                else
                    Toast.makeText(AdminActivity.this, "Backup error occurred", Toast.LENGTH_LONG).show();

            }
        });

        buttonClearDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearDatabase();
            }
        });
    }

    private void ShowBackupDialog(final File physicalFile, final File rescanFile)
    {
       new AlertDialog.Builder(this)
            .setTitle("Backup Complete!")
            .setMessage("You can find the backup files in the root of the file system in a folder called \"cphmobile\"." +
            " Use the File Browser app in your app drawer to navigate to the files. Would you like to upload the files to the CPH server?")
            .setIcon(android.R.drawable.ic_dialog_alert)
               .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                   public void onClick(DialogInterface dialog, int whichButton) {
                       uploadFile(physicalFile);
                       uploadFile(rescanFile);
                   }})
               .setNegativeButton(android.R.string.no, null).show();
    }

    private void ClearDatabase()
    {
        new AlertDialog.Builder(this)
            .setTitle("ALERT!")
            .setMessage("Are you sure you want to clear all database data? This cannot be undone!")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                    DBVehicleEntry.clearPhysicalTable(dbHelper);
                    DBRescan.clearRescanTable(dbHelper);

                    Toast.makeText(AdminActivity.this, "Annnnd itss gooonne!", Toast.LENGTH_SHORT).show();

                }})
            .setNegativeButton(android.R.string.no, null).show();
    }

    public void onBackPressed()
    {
        Toast toast = Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 75);
        toast.show();

        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void uploadFile(File fileName){


        FTPClient client = new FTPClient();

        try {

            client.connect(FTP_HOST);
            client.login(FTP_USER, FTP_PASS);
            //client.setType(FTPClient.TYPE_BINARY);

            client.upload(fileName, new MyTransferListener());

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {

            //btn.setVisibility(View.GONE);
            // Transfer started
            Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
            //System.out.println(" Upload Started ...");
        }

        public void transferred(int length) {

            // Yet other length bytes has been transferred since the last time this
            // method was called
            Toast.makeText(getBaseContext(), " transferred ..." + length, Toast.LENGTH_SHORT).show();
            //System.out.println(" transferred ..." + length);
        }

        public void completed() {

            //btn.setVisibility(View.VISIBLE);
            // Transfer completed

            Toast.makeText(getBaseContext(), " completed ...", Toast.LENGTH_SHORT).show();
            //System.out.println(" completed ..." );
        }

        public void aborted() {

            //btn.setVisibility(View.VISIBLE);
            // Transfer aborted
            Toast.makeText(getBaseContext()," transfer aborted , please try again...", Toast.LENGTH_SHORT).show();
            //System.out.println(" aborted ..." );
        }

        public void failed() {

            //btn.setVisibility(View.VISIBLE);
            // Transfer failed
            System.out.println(" failed ..." );
        }

    }
}
