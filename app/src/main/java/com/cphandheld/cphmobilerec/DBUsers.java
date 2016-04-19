package com.cphandheld.cphmobilerec;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by titan on 4/15/16.
 */
public class DBUsers {

    public static final String USERS_TABLE_NAME = "users";
    public static final String USERS_COLUMN_ID = "id";
    public static final String USERS_COLUMN_FIRST_NAME = "firstname";
    public static final String USERS_COLUMN_LAST_NAME = "lastname";
    public static final String USERS_COLUMN_PIN = "pin";

    public static final String DEALERSHIPS_TABLE_NAME = "dealership";
    public static final String DEALERSHIPS_COLUMN_USER_ID = "userid";
    public static final String DEALERSHIPS_COLUMN_ID = "id";
    public static final String DEALERSHIPS_COLUMN_DEALER_CODE = "dealercode";
    public static final String DEALERSHIPS_COLUMN_NAME = "name";
    public static final String DEALERSHIPS_COLUMN_LOT_1_NAME = "lot1name";
    public static final String DEALERSHIPS_COLUMN_LOT_2_NAME = "lot2name";
    public static final String DEALERSHIPS_COLUMN_LOT_3_NAME = "lot3name";
    public static final String DEALERSHIPS_COLUMN_LOT_4_NAME = "lot4name";
    public static final String DEALERSHIPS_COLUMN_LOT_5_NAME = "lot5name";
    public static final String DEALERSHIPS_COLUMN_LOT_6_NAME = "lot6name";
    public static final String DEALERSHIPS_COLUMN_LOT_7_NAME = "lot7name";
    public static final String DEALERSHIPS_COLUMN_LOT_8_NAME = "lot8name";
    public static final String DEALERSHIPS_COLUMN_LOT_9_NAME = "lot9name";

    public static Cursor getUserByPin(DBHelper dbh, int pin){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c =  db.rawQuery( "select * from " + USERS_TABLE_NAME + " where pin="+pin+"", null );
        return c;
    }

    public static boolean isUserStored(DBHelper dbh, String pin) {
        Cursor c = null;
        SQLiteDatabase db = dbh.getReadableDatabase();
        boolean result = false;
        try {

            String query = "select count(*) from " + USERS_TABLE_NAME + " where pin = ?";
            c = db.rawQuery(query, new String[] {pin});
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

    public static boolean insertUser(DBHelper dbh, int userId, int pin, String firstName, String lastName)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COLUMN_ID, userId);
        contentValues.put(USERS_COLUMN_PIN, pin);
        contentValues.put(USERS_COLUMN_FIRST_NAME, firstName);
        contentValues.put(USERS_COLUMN_LAST_NAME, lastName);

        // this is an insert/update
        db.insertWithOnConflict(USERS_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public static boolean insertDealership(DBHelper dbh, int userId, int id, String name, String dealerCode, String lot1Name, String lot2Name, String lot3Name, String lot4Name, String lot5Name, String lot6Name, String lot7Name, String lot8Name, String lot9Name)
    {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DEALERSHIPS_COLUMN_USER_ID, userId);
        contentValues.put(DEALERSHIPS_COLUMN_ID, id);
        contentValues.put(DEALERSHIPS_COLUMN_NAME, name);
        contentValues.put(DEALERSHIPS_COLUMN_DEALER_CODE, dealerCode);
        contentValues.put(DEALERSHIPS_COLUMN_LOT_1_NAME, lot1Name);
        contentValues.put(DEALERSHIPS_COLUMN_LOT_2_NAME, lot2Name);
        contentValues.put(DEALERSHIPS_COLUMN_LOT_3_NAME, lot3Name);
        contentValues.put(DEALERSHIPS_COLUMN_LOT_4_NAME, lot4Name);
        contentValues.put(DEALERSHIPS_COLUMN_LOT_5_NAME, lot5Name);
        contentValues.put(DEALERSHIPS_COLUMN_LOT_6_NAME, lot6Name);
        contentValues.put(DEALERSHIPS_COLUMN_LOT_7_NAME, lot7Name);
        contentValues.put(DEALERSHIPS_COLUMN_LOT_8_NAME, lot8Name);
        contentValues.put(DEALERSHIPS_COLUMN_LOT_9_NAME, lot9Name);


        // this is an insert/update
        db.insertWithOnConflict(DEALERSHIPS_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public static Cursor getDealershipsByUser(DBHelper dbh, String userId){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + DEALERSHIPS_TABLE_NAME + " where " + DEALERSHIPS_COLUMN_USER_ID + " = ? order by id DESC" , new String[] {userId});
        return res;
    }
}
