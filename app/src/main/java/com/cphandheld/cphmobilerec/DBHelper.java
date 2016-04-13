package com.cphandheld.cphmobilerec;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

        db.execSQL("create table " + USER_TABLE_NAME + " (userId integer primary key, pin text, organizationId text, name text)");
        db.execSQL("create table " + ORGANIZATION_TABLE_NAME + " ( id integer primary key autoincrement, organizationId text, name text)");
        db.execSQL("create table " + VEHICLE_ENTRY_TABLE_NAME + " ( id integer primary key autoincrement, vin text, dealership text, newused text, entrytype text, lot text, date text, time text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ORGANIZATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VEHICLE_ENTRY_TABLE_NAME);
        onCreate(db);
    }
}
