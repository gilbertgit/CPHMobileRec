package com.cphandheld.cphmobilerec;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by titan on 4/10/16.
 */
public class DBVehicleEntry {
    public static final String VEHICLE_ENTRY_TABLE_NAME = "vehicleentry";
    public static final String VEHICLE_ENTRY_COLUMN_ID = "id";
    public static final String VEHICLE_ENTRY_COLUMN_USER_ID = "userId";
    public static final String VEHICLE_ENTRY_COLUMN_VIN = "vin";
    public static final String VEHICLE_ENTRY_COLUMN_DEALERSHIP = "dealership";
    public static final String VEHICLE_ENTRY_COLUMN_NEW_USED = "newused";
    public static final String VEHICLE_ENTRY_COLUMN_ENTRY_TYPE = "entrytype";
    public static final String VEHICLE_ENTRY_COLUMN_LOT = "lot";
    public static final String VEHICLE_ENTRY_COLUMN_DATE = "date";
    public static final String VEHICLE_ENTRY_COLUMN_TIME = "time";
    public static final String VEHICLE_ENTRY_COLUMN_NOTES = "notes";
    public static final String VEHICLE_ENTRY_COLUMN_LATITUDE = "latitude";
    public static final String VEHICLE_ENTRY_COLUMN_LONGITUDE = "longitude";

    public static boolean insertVehicleEntry(DBHelper dbh, String vin, String dealership, String newUsed, String entryType, String lot, String date, String time, String userId, String lat, String lng)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VEHICLE_ENTRY_COLUMN_VIN, vin);
        contentValues.put(VEHICLE_ENTRY_COLUMN_DEALERSHIP, dealership);
        contentValues.put(VEHICLE_ENTRY_COLUMN_NEW_USED, newUsed);
        contentValues.put(VEHICLE_ENTRY_COLUMN_ENTRY_TYPE, entryType);
        contentValues.put(VEHICLE_ENTRY_COLUMN_LOT, lot);
        contentValues.put(VEHICLE_ENTRY_COLUMN_DATE, date);
        contentValues.put(VEHICLE_ENTRY_COLUMN_TIME, time);
        contentValues.put(VEHICLE_ENTRY_COLUMN_NOTES, "");
        contentValues.put(VEHICLE_ENTRY_COLUMN_USER_ID, userId);
        contentValues.put(VEHICLE_ENTRY_COLUMN_LATITUDE, lat);
        contentValues.put(VEHICLE_ENTRY_COLUMN_LONGITUDE, lng);

        db.insertWithOnConflict(VEHICLE_ENTRY_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return true;
    }

    public static boolean updateVehicleEntry(DBHelper dbh, boolean isMultiUpdate, String vin, String dealership, String newUsed, String entryType, String lot, String date, String time, String notes, String userId)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VEHICLE_ENTRY_COLUMN_DEALERSHIP, dealership);
        contentValues.put(VEHICLE_ENTRY_COLUMN_NEW_USED, newUsed);
        // we do not want to alter the entry method
        //contentValues.put(VEHICLE_ENTRY_COLUMN_ENTRY_TYPE, entryType);
        contentValues.put(VEHICLE_ENTRY_COLUMN_LOT, lot);
        contentValues.put(VEHICLE_ENTRY_COLUMN_DATE, date);
        contentValues.put(VEHICLE_ENTRY_COLUMN_TIME, time);
        if(isMultiUpdate && !notes.equals(""))
            contentValues.put(VEHICLE_ENTRY_COLUMN_NOTES, notes);
        else if(!isMultiUpdate)
            contentValues.put(VEHICLE_ENTRY_COLUMN_NOTES, notes);
        contentValues.put(VEHICLE_ENTRY_COLUMN_USER_ID, userId);

        db.update(VEHICLE_ENTRY_TABLE_NAME, contentValues, "vin = ?", new String[]{vin});
        return true;
    }

    public static boolean updateEntry(DBHelper dbh, String vin, String date, String time, String lat, String lng)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VEHICLE_ENTRY_COLUMN_DATE, date);
        contentValues.put(VEHICLE_ENTRY_COLUMN_TIME, time);
        contentValues.put(VEHICLE_ENTRY_COLUMN_LATITUDE, lat);
        contentValues.put(VEHICLE_ENTRY_COLUMN_LONGITUDE, lng);

        db.update(VEHICLE_ENTRY_TABLE_NAME, contentValues, "vin = ?", new String[]{vin});
        return true;
    }

    public static Cursor getPhysical(DBHelper dbh){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + VEHICLE_ENTRY_TABLE_NAME + " order by id DESC" , null );
        return res;
    }

    public static boolean isVinScanned(DBHelper dbh, String vin) {
        Cursor c = null;
        SQLiteDatabase db = dbh.getReadableDatabase();
        boolean result = false;
        try {

            String query = "select count(*) from " + VEHICLE_ENTRY_TABLE_NAME + " where vin = ?";
            c = db.rawQuery(query, new String[] {vin});
            if (c.moveToFirst()) {
                if(c.getInt(0) != 0)
                    result = true;
            }
        }
        finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }

            return result;
        }
    }

    public static Cursor getPhysicalByDealership(Context c, DBHelper dbh, String dealership){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        boolean sort = prefs.getBoolean(SettingsActivity.KEY_PREF_SORTBY_LASTUPDATE, false);
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor cursor;
        if(sort)
            cursor = db.rawQuery( "select * from " + VEHICLE_ENTRY_TABLE_NAME + " where " + VEHICLE_ENTRY_COLUMN_DEALERSHIP + " = ? order by id DESC" , new String[] {dealership});
        else
            cursor = db.rawQuery( "select * from " + VEHICLE_ENTRY_TABLE_NAME + " where " + VEHICLE_ENTRY_COLUMN_DEALERSHIP + " = ? order by date DESC, time DESC" , new String[] {dealership});
        return cursor;
    }

    public static Cursor getPhysicalByUser(DBHelper dbh, String user){
        SQLiteDatabase db = dbh.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from " + VEHICLE_ENTRY_TABLE_NAME + " where " + VEHICLE_ENTRY_COLUMN_DEALERSHIP + " = ? order by id DESC" , new String[] {dealership});

        Cursor res =  db.rawQuery( "select * from " + VEHICLE_ENTRY_TABLE_NAME + " where " + VEHICLE_ENTRY_COLUMN_USER_ID + " = ? order by date DESC, time DESC" , new String[] {user});
        return res;
    }

    public static void clearPhysicalTable(DBHelper dbh)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.execSQL("DELETE FROM " + VEHICLE_ENTRY_TABLE_NAME );
    }

    public static void clearPhysicalTableByUser(DBHelper dbh, String user)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.delete(VEHICLE_ENTRY_TABLE_NAME,
                VEHICLE_ENTRY_COLUMN_USER_ID + " = ? ",
                new String[] {user});
    }

    public static void removePhysicalByVin(DBHelper dbh, String vin)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.delete(VEHICLE_ENTRY_TABLE_NAME,
                VEHICLE_ENTRY_COLUMN_VIN + " = ? ",
                new String[] {vin});
    }

    public static Cursor getEntryByVin(DBHelper dbh, String vin)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        Cursor c = db.rawQuery("select * from " + VEHICLE_ENTRY_TABLE_NAME + " where " + VEHICLE_ENTRY_COLUMN_VIN + " = ? ",
                new String[] {vin});

        return c;
    }

    public static ArrayList GetPhysicalForUpload(DBHelper dbh, String user) {

        ArrayList physical = new ArrayList();

        Cursor c = DBVehicleEntry.getPhysicalByUser(dbh, user);

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
                physical.add(phy);
            } while (c.moveToNext());
        }
        c.close();

        return  physical;
    }

    public static void BackupPhysicalDB(DBHelper dbh, Context context, String userId) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yy");
        SimpleDateFormat tf = new SimpleDateFormat("h-mm-ss");
        String formattedDate = df.format(c.getTime());
        String formattedTime = tf.format(c.getTime());

        SQLiteDatabase db = dbh.getWritableDatabase();

        File dbFile =  context.getDatabasePath(DBHelper.DATABASE_NAME);
        File exportDir = new File(Environment.getExternalStorageDirectory()+"/cphmobile/", "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "REC-" + formattedDate + "-" + formattedTime +".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Cursor curCSV = db.rawQuery("SELECT * FROM " + VEHICLE_ENTRY_TABLE_NAME + " where userId = ?" , new String[] {userId} );
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3), curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6), curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9), curCSV.getString(10), curCSV.getString(11)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("ExportData", sqlEx.getMessage(), sqlEx);
        }
    }

    public static File BackupPhysicalDBAdmin(DBHelper dbh) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yy");
        SimpleDateFormat tf = new SimpleDateFormat("h-mm-ss");
        String formattedDate = df.format(c.getTime());
        String formattedTime = tf.format(c.getTime());

        SQLiteDatabase db = dbh.getWritableDatabase();

        File exportDir = new File(Environment.getExternalStorageDirectory()+"/cphmobile/", "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        String fileName = "REC-" + formattedDate + "-" + formattedTime +".csv";

        File file = new File(exportDir, fileName);
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Cursor curCSV = db.rawQuery("SELECT * FROM " + VEHICLE_ENTRY_TABLE_NAME, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3), curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6), curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9), curCSV.getString(10), curCSV.getString(11)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            return file;
        } catch (Exception sqlEx) {
            Log.e("ExportData", sqlEx.getMessage(), sqlEx);
            return null;
        }
    }
}
