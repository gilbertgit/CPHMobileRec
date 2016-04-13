package com.cphandheld.cphmobilerec;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by titan on 4/8/16.
 */
public class DBOrganizations {

    public static final String ORGANIZATION_TABLE_NAME = "organizations";
    public static final String ORGANIZATION_COLUMN_ORGANIZATION_ID = "organizationId";
    public static final String ORGANIZATION_COLUMN_NAME = "name";

    public static boolean insertOrganization(DBHelper dbh, int organizationId, String name)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORGANIZATION_COLUMN_ORGANIZATION_ID, organizationId);
        contentValues.put(ORGANIZATION_COLUMN_NAME, name);

        db.insert(ORGANIZATION_TABLE_NAME, null, contentValues);
        return true;
    }

    public static Cursor getOrganizations(DBHelper dbh){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ORGANIZATION_TABLE_NAME , null );
        return res;
    }

    public static void clearOrganizationTable(DBHelper dbh)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.execSQL("DELETE FROM " + ORGANIZATION_TABLE_NAME);
    }
}
