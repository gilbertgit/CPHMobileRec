package com.cphandheld.cphmobilerec;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by titan on 4/8/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CPHMobileDB.db";

    public static final String USER_TABLE_NAME = "users";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_USER_ID = "userId";
    public static final String USER_COLUMN_PIN = "pin";
    public static final String USER_COLUMN_ORGANIZATION_ID = "organizationId";
    public static final String USER_COLUMN_NAME = "name";

    public static final String ORGANIZATION_TABLE_NAME = "organizations";
    public static final String ORGANIZATION_COLUMN_ORGANIZATION_ID = "organizationId";
    public static final String ORGANIZATION_COLUMN_NAME = "name";

    public static final String VEHICLE_ENTRY_TABLE_NAME = "vehicleentry";
    public static final String DEALERSHIPS_TABLE_NAME = "dealership";

    protected SQLiteDatabase sqdb;
    DBHelper dbHelper;


    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.dbHelper = this;
        this.sqdb = dbHelper.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + USER_TABLE_NAME + " (id integer primary key, firstname text, lastname text, pin text)");
        db.execSQL("create table " + ORGANIZATION_TABLE_NAME + " ( id integer primary key autoincrement, organizationId text, name text)");
        db.execSQL("create table " + VEHICLE_ENTRY_TABLE_NAME + " ( id integer primary key autoincrement, vin text, dealership text, newused                text, entrytype text, lot text, date text, time text, notes text)");
        db.execSQL("create table " + DEALERSHIPS_TABLE_NAME + " (id integer primary key, userid text, dealercode text, name text, lot1name text, lot2name text, lot3name text, lot4name text, lot5name text, lot6name text, lot7name text, lot8name text, lot9name text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ORGANIZATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VEHICLE_ENTRY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DEALERSHIPS_TABLE_NAME);
        onCreate(db);
    }

    public void ImportData(Context context)
    {
//        File importDir = new File(Environment.getExternalStorageDirectory(), "");
//        File file = new File(importDir, "test-scan.csv");
//        FileReader fileReader = new FileReader(file);
        //BufferedReader buffer = new BufferedReader(fileReader);
        //String line = "";

        sqdb.execSQL("DELETE FROM " + VEHICLE_ENTRY_TABLE_NAME );

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
        SimpleDateFormat tf = new SimpleDateFormat("h:mm:ss aa");


        String mCSVfile = "test-scans.csv";
        AssetManager manager = context.getAssets();
        InputStream inStream = null;
        try {
            inStream = manager.open(mCSVfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line = "";
        sqdb.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] colums = line.split(",");
                Date formattedTime = new Date();
                Date formattedDate = new Date();
                if (colums.length != 7) {
                    Log.d("CSVParser", "Skipping Bad CSV Row");
                    continue;
                }
                ContentValues cv = new ContentValues(3);
                cv.put("dealership", colums[0].trim());
                cv.put("vin", colums[1].trim());
                cv.put("newused", colums[2].trim());
                cv.put("lot", colums[3].trim());
                cv.put("entrytype", colums[4].trim());
//                try
//                {
//                 formattedDate = df.parse(colums[5].trim());
//                 formattedTime = tf.parse(colums[5].trim());
//                } catch (ParseException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
                cv.put("date", colums[5].trim());
                cv.put("time", colums[6].trim());
                cv.put("notes", "");
                sqdb.insert(VEHICLE_ENTRY_TABLE_NAME, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqdb.setTransactionSuccessful();
        sqdb.endTransaction();
    }

    public void ExportDB(Context context) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yy");
        String formattedDate = df.format(c.getTime());

        File dbFile =  context.getDatabasePath(DATABASE_NAME);
        File exportDir = new File(Environment.getExternalStorageDirectory()+"/cphmobile/", "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "REC" + formattedDate +".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = sqdb.rawQuery("SELECT * FROM " + VEHICLE_ENTRY_TABLE_NAME, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("ExportData", sqlEx.getMessage(), sqlEx);
        }
    }
}
