package com.cphandheld.cphmobilerec;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by titan on 4/10/16.
 */
public class DBVehicleEntry {
    public static final String VEHICLE_ENTRY_TABLE_NAME = "vehicleentry";
    public static final String VEHICLE_ENTRY_COLUMN_ID = "id";
    public static final String VEHICLE_ENTRY_COLUMN_VIN = "vin";
    public static final String VEHICLE_ENTRY_COLUMN_DEALERSHIP = "dealership";
    public static final String VEHICLE_ENTRY_COLUMN_NEW_USED = "newused";
    public static final String VEHICLE_ENTRY_COLUMN_ENTRY_TYPE = "entrytype";
    public static final String VEHICLE_ENTRY_COLUMN_LOT = "lot";
    public static final String VEHICLE_ENTRY_COLUMN_DATE = "date";
    public static final String VEHICLE_ENTRY_COLUMN_TIME = "time";
    public static final String VEHICLE_ENTRY_COLUMN_NOTES = "notes";

    public static boolean insertVehicleEntry(DBHelper dbh, String vin, String dealership, String newUsed, String entryType, String lot, String date, String time)
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

        db.insertWithOnConflict(VEHICLE_ENTRY_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

       // db.insert(VEHICLE_ENTRY_TABLE_NAME, null, contentValues);
        return true;
    }

    public static boolean updateVehicleEntry(DBHelper dbh, String vin, String dealership, String newUsed, String entryType, String lot, String date, String time, String notes)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VEHICLE_ENTRY_COLUMN_DEALERSHIP, dealership);
        contentValues.put(VEHICLE_ENTRY_COLUMN_NEW_USED, newUsed);
        contentValues.put(VEHICLE_ENTRY_COLUMN_ENTRY_TYPE, entryType);
        contentValues.put(VEHICLE_ENTRY_COLUMN_LOT, lot);
        contentValues.put(VEHICLE_ENTRY_COLUMN_DATE, date);
        contentValues.put(VEHICLE_ENTRY_COLUMN_TIME, time);
        contentValues.put(VEHICLE_ENTRY_COLUMN_NOTES, notes);

        db.update(VEHICLE_ENTRY_TABLE_NAME, contentValues, "vin = ?", new String[]{vin});
        return true;
    }

    public static boolean updateEntryDate(DBHelper dbh, String vin, String date, String time)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VEHICLE_ENTRY_COLUMN_DATE, date);
        contentValues.put(VEHICLE_ENTRY_COLUMN_TIME, time);

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

    public static Cursor getPhysicalByDealership(DBHelper dbh, String dealership){
        SQLiteDatabase db = dbh.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from " + VEHICLE_ENTRY_TABLE_NAME + " where " + VEHICLE_ENTRY_COLUMN_DEALERSHIP + " = ? order by id DESC" , new String[] {dealership});

        Cursor res =  db.rawQuery( "select * from " + VEHICLE_ENTRY_TABLE_NAME + " where " + VEHICLE_ENTRY_COLUMN_DEALERSHIP + " = ? order by date DESC, time DESC" , new String[] {dealership});
        return res;
    }

    public static void clearPhysicalTable(DBHelper dbh)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.execSQL("DELETE FROM " + VEHICLE_ENTRY_TABLE_NAME );
    }

    public static void removePhysicalByVin(DBHelper dbh, String vin)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        //db.execSQL("DELETE FROM " + VEHICLE_ENTRY_TABLE_NAME + " WHERE vin=" + vin);
        db.delete(VEHICLE_ENTRY_TABLE_NAME,
                VEHICLE_ENTRY_COLUMN_VIN + " = ? ",
                new String[] {vin});
    }
}
