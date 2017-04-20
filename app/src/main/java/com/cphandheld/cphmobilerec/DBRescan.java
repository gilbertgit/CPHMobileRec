package com.cphandheld.cphmobilerec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by titan on 5/31/16.
 */
public class DBRescan {

    public static final String RESCAN_TABLE_NAME = "rescan";
    public static final String RESCAN_COLUMN_SIID = "siid";
    public static final String RESCAN_COLUMN_VIN = "vin";
    public static final String RESCAN_COLUMN_ASSIGNED = "assigned";
    public static final String RESCAN_COLUMN_YEAR = "year";
    public static final String RESCAN_COLUMN_MAKE = "make";
    public static final String RESCAN_COLUMN_MODEL = "model";
    public static final String RESCAN_COLUMN_COLOR = "color";
    public static final String RESCAN_COLUMN_ENTRY_METHOD = "entryMethod";
    public static final String RESCAN_COLUMN_DEALERCODE = "dealerCode";
    public static final String RESCAN_COLUMN_SCANNED_DATE = "scannedDate";
    public static final String RESCAN_COLUMN_SCANNED_BY = "scannedBy";
    public static final String RESCAN_COLUMN_USER_ID = "userId";
    public static final String RESCAN_COLUMN_LATITUDE = "latitude";
    public static final String RESCAN_COLUMN_LONGITUDE = "longitude";

    public static boolean insertRescan(DBHelper dbh, String siid, String dealerCode, String vin, String assigned, String year, String make, String model, String color, String entryMethod, String scannedDate, String userId) {
        // siid, dealerCode, vin, assigned, year, make, model, color,entryMethod, scannedDate, userId
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RESCAN_COLUMN_SIID, siid);
        contentValues.put(RESCAN_COLUMN_DEALERCODE, dealerCode);
        contentValues.put(RESCAN_COLUMN_VIN, vin);
        contentValues.put(RESCAN_COLUMN_ASSIGNED, assigned);
        contentValues.put(RESCAN_COLUMN_YEAR, year);
        contentValues.put(RESCAN_COLUMN_MAKE, make);
        contentValues.put(RESCAN_COLUMN_MODEL, model);
        contentValues.put(RESCAN_COLUMN_COLOR, color);
        contentValues.put(RESCAN_COLUMN_ENTRY_METHOD, entryMethod);
        contentValues.put(RESCAN_COLUMN_SCANNED_DATE, scannedDate);
        contentValues.put(RESCAN_COLUMN_USER_ID, userId);

        db.insertWithOnConflict(RESCAN_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        return true;
    }

    public static boolean updateRescanByVin(DBHelper dbh, String vin, String entryMethod, String scannedDate, String scannedBy, String userId, String lat, String lng) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RESCAN_COLUMN_ENTRY_METHOD, entryMethod);
        contentValues.put(RESCAN_COLUMN_SCANNED_DATE, scannedDate);
        //contentValues.put(RESCAN_COLUMN_SCANNED_BY, scannedBy);
        contentValues.put(RESCAN_COLUMN_USER_ID, userId);
        contentValues.put(RESCAN_COLUMN_LATITUDE, lat);
        contentValues.put(RESCAN_COLUMN_LONGITUDE, lng);

        db.update(RESCAN_TABLE_NAME, contentValues, "vin = ?", new String[]{vin});

        return true;
    }

    public static Cursor getRescanToScan(DBHelper dbh, String dealership) {
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + RESCAN_TABLE_NAME + " where " + RESCAN_COLUMN_DEALERCODE + " = ? and " + RESCAN_COLUMN_SCANNED_DATE + " is null or " + RESCAN_COLUMN_SCANNED_DATE + " = '' order by make DESC", new String[]{dealership});
        return res;
    }

    public static Cursor getCompletedRescans(DBHelper dbh, String dealership) {
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + RESCAN_TABLE_NAME + " where " + RESCAN_COLUMN_DEALERCODE + " = ? and " + RESCAN_COLUMN_SCANNED_DATE + " != '' order by scannedDate DESC", new String[]{dealership});
        return res;
    }

    public static Cursor getCompletedRescansByUser(DBHelper dbh, String user) {
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + RESCAN_TABLE_NAME + " where " + RESCAN_COLUMN_USER_ID + " = ? and " + RESCAN_COLUMN_SCANNED_DATE + " != '' order by scannedDate DESC", new String[]{user});
        return res;
    }

    public static int getRescanCompletedCountByDealerCode(DBHelper dbh, String dealerCode)
    {
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("select id from " + RESCAN_TABLE_NAME + " where " + RESCAN_COLUMN_SCANNED_DATE + " != '' and " + RESCAN_COLUMN_DEALERCODE + " = ?", new String[]{dealerCode});
        c.moveToFirst();
        int count = c.getCount();
        c.close();
        return count;
    }

    public static int getRescanCountByDealerCode(DBHelper dbh, String dealerCode)
    {
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("select id from " + RESCAN_TABLE_NAME + " where (" + RESCAN_COLUMN_SCANNED_DATE + " is null or " + RESCAN_COLUMN_SCANNED_DATE + " = '') and " + RESCAN_COLUMN_DEALERCODE + " = ?", new String[]{dealerCode});
        c.moveToFirst();
        int count = c.getCount();
        c.close();
        return count;
    }

    public static boolean uploadReady(DBHelper dbh) {
        Cursor c = null;
        SQLiteDatabase db = dbh.getReadableDatabase();
        boolean result = false;
        try {

            String query = "select count(*) from " + RESCAN_TABLE_NAME + " where scannedDate != ''";
            c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                if (c.getInt(0) != 0)
                    result = true;
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }

            return result;
        }
    }
    public static void clearRescanTable(DBHelper dbh)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.execSQL("DELETE FROM " + RESCAN_TABLE_NAME );
    }


    public static void deleteRescanByUser(DBHelper dbh, String user) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.delete(RESCAN_TABLE_NAME,
                RESCAN_COLUMN_USER_ID + "=? ",
                new String[]{user});
    }

    public static void deleteRescan(DBHelper dbh, String siid) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.delete(RESCAN_TABLE_NAME,
                RESCAN_COLUMN_SIID + " = ? ",
                new String[]{siid});
    }

    public static Rescan setRescanData(Cursor c) {
        int siidIndex = c.getColumnIndex("siid");
        String siid = c.getString(siidIndex);

        int vinIndex = c.getColumnIndex("vin");
        String vin = c.getString(vinIndex);

        int assignedIndex = c.getColumnIndex("assigned");
        String assigned = c.getString(assignedIndex);

        int dealerCodeIndex = c.getColumnIndex("dealerCode");
        String dealerCode = c.getString(dealerCodeIndex);

        int entryMethodIndex = c.getColumnIndex("entryMethod");
        String entryMethod = c.getString(entryMethodIndex);

        int yearIndex = c.getColumnIndex("year");
        String year = c.getString(yearIndex);

        int makeIndex = c.getColumnIndex("make");
        String make = c.getString(makeIndex);

        int modelIndex = c.getColumnIndex("model");
        String model = c.getString(modelIndex);

        int colorIndex = c.getColumnIndex("color");
        String color = c.getString(colorIndex);

        int scannedDateIndex = c.getColumnIndex("scannedDate");
        String scannedDate = c.getString(scannedDateIndex);

        int userIdIndex = c.getColumnIndex("userId");
        String userId = c.getString(userIdIndex);

        return new Rescan(siid, dealerCode, vin, assigned, year, make, model, color, entryMethod, scannedDate, userId);

    }

    public static ArrayList getRescanForUpload(DBHelper dbh, String user) {
        Cursor c = DBRescan.getCompletedRescansByUser(dbh, user);

        ArrayList rescan = new ArrayList();

        if (c.moveToFirst()) {
            do {
                int siidIndex = c.getColumnIndex("siid");
                String siid = c.getString(siidIndex);

                int vinIndex = c.getColumnIndex("vin");
                String vin = c.getString(vinIndex);

                int assignedIndex = c.getColumnIndex("assigned");
                String assigned = c.getString(assignedIndex);

                int dealerCodeIndex = c.getColumnIndex("dealerCode");
                String dealerCode = c.getString(dealerCodeIndex);

                int entryMethodIndex = c.getColumnIndex("entryMethod");
                String entryMethod = c.getString(entryMethodIndex);

                int scannedDateIndex = c.getColumnIndex("scannedDate");
                String scannedDate = c.getString(scannedDateIndex);

                int latitudeIndex = c.getColumnIndex("latitude");
                String latitude = c.getString(latitudeIndex);

                int longitudeIndex = c.getColumnIndex("longitude");
                String longitude = c.getString(longitudeIndex);

                int userIdIndex = c.getColumnIndex("userId");
                String userId = c.getString(userIdIndex);

                RescanComplete res = new RescanComplete(dealerCode, siid, entryMethod, vin, latitude, longitude, scannedDate);

                rescan.add(res);
            } while (c.moveToNext());
        }
        c.close();

        return rescan;

    }

    public static boolean BackupRescanDB(DBHelper dbh, Context context, String user) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yy");
        SimpleDateFormat tf = new SimpleDateFormat("h-mm-ss");
        String formattedDate = df.format(c.getTime());
        String formattedTime = tf.format(c.getTime());

        SQLiteDatabase db = dbh.getReadableDatabase();

        File dbFile =  context.getDatabasePath(DBHelper.DATABASE_NAME);
        File exportDir = new File(Environment.getExternalStorageDirectory()+"/cphmobile/", "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "RESCAN-" + formattedDate + "-" + formattedTime +".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Cursor curCSV = db.rawQuery("SELECT * FROM " + RESCAN_TABLE_NAME + " WHERE userid = ?", new String[]{user});
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            return true;
        } catch (Exception sqlEx) {
            Log.e("ExportData", sqlEx.getMessage(), sqlEx);
        }

        return false;
    }

    public static File BackupRescanDBAdmin(DBHelper dbh) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yy");
        SimpleDateFormat tf = new SimpleDateFormat("h-mm-ss");
        String formattedDate = df.format(c.getTime());
        String formattedTime = tf.format(c.getTime());

        SQLiteDatabase db = dbh.getReadableDatabase();

        File exportDir = new File(Environment.getExternalStorageDirectory()+"/cphmobile/", "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        String fileName = "RESCAN-" + formattedDate + "-" + formattedTime +".csv";
        File file = new File(exportDir, fileName);
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Cursor curCSV = db.rawQuery("SELECT * FROM " + RESCAN_TABLE_NAME,null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            return file;
        } catch (Exception sqlEx) {
            Log.e("ExportData", sqlEx.getMessage(), sqlEx);
        }

        return null;
    }
}
