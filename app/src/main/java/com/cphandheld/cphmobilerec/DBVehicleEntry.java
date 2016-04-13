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

        db.insert(VEHICLE_ENTRY_TABLE_NAME, null, contentValues);
        return true;
    }

    public static Cursor getPhysical(DBHelper dbh){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + VEHICLE_ENTRY_TABLE_NAME + " order by id DESC" , null );
        return res;
    }

    public static Cursor getPhysicalByDealership(DBHelper dbh, String dealership){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + VEHICLE_ENTRY_TABLE_NAME + " where " + VEHICLE_ENTRY_COLUMN_VIN + " = ? order by id DESC" , new String[] {dealership});
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
