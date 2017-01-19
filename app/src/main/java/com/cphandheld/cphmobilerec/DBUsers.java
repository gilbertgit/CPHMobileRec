package com.cphandheld.cphmobilerec;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

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

    public static boolean hasFilteredDealerships(DBHelper dbh, String userId) {
        Cursor c = null;
        SQLiteDatabase db = dbh.getReadableDatabase();
        boolean result = false;
        try {

            String query = "select * from " + DBHelper.DEALERSHIPS_SELECTED_TABLE_NAME + " where " + DEALERSHIPS_COLUMN_USER_ID + " = ?";

            c = db.rawQuery(query, new String[] {userId});
            int count  = c.getCount();
            boolean test = count > 0;
            if(test) {
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

    public static boolean isDealershipStored(DBHelper dbh, String dealerCode, String userId) {
        Cursor c = null;
        SQLiteDatabase db = dbh.getReadableDatabase();
        boolean result = true;
        try {

            String query = "select * from " + DEALERSHIPS_TABLE_NAME + " where " + DEALERSHIPS_COLUMN_DEALER_CODE + " = ? and " + DEALERSHIPS_COLUMN_USER_ID + " = ?";

            c = db.rawQuery(query, new String[] {dealerCode, userId});
            int count  = c.getCount();
            boolean test = count >= 1;
            if(!test) {
                result = false;
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

    public static boolean insertSelectedDealership(DBHelper dbh, int userId, int id, String name, String dealerCode, String lot1Name, String lot2Name, String lot3Name, String lot4Name, String lot5Name, String lot6Name, String lot7Name, String lot8Name, String lot9Name)
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
        db.insertWithOnConflict(DBHelper.DEALERSHIPS_SELECTED_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }


    public static Cursor getDealershipsByUser(DBHelper dbh, String userId){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + DEALERSHIPS_TABLE_NAME + " where " + DEALERSHIPS_COLUMN_USER_ID + " = ? order by name ASC" , new String[] {userId});
        return res;
    }

    public static Cursor getFilteredDealershipsByUser(DBHelper dbh, String userId){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + DBHelper.DEALERSHIPS_SELECTED_TABLE_NAME + " where " + DEALERSHIPS_COLUMN_USER_ID + " = ? order by name ASC" , new String[] {userId});
        return res;
    }

    public static boolean deleteFilteredDealership(DBHelper dbh, String dealerCode)
    {
        SQLiteDatabase db = dbh.getReadableDatabase();
        return db.delete(DBHelper.DEALERSHIPS_SELECTED_TABLE_NAME, DEALERSHIPS_COLUMN_DEALER_CODE + " = ?", new String[] {dealerCode}) > 0;
    }

    public static boolean clearDealerships(DBHelper dbh, String userId)
    {
        SQLiteDatabase db = dbh.getReadableDatabase();
        return db.delete(DEALERSHIPS_TABLE_NAME, DEALERSHIPS_COLUMN_USER_ID + " = ?", new String[] {userId}) > 0;
    }

    public static Dealership getFilteredDealership(DBHelper dbh, String dealerCode)
    {
        Dealership dealership = null;
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + DBHelper.DEALERSHIPS_SELECTED_TABLE_NAME + " where " + DEALERSHIPS_COLUMN_DEALER_CODE + " = ? " , new String[] {dealerCode});

        if(c.getCount() > 0)
            dealership = setDealershipData(c);

            return dealership;
    }

    public static ArrayList<Dealership> setDealershipDataList(Cursor c)
    {
        ArrayList<Dealership> data = new ArrayList<>(c.getCount());

        if (c.moveToFirst()) {
            do {

                Dealership d = setDealershipData(c);
                data.add(d);
            } while (c.moveToNext());
        }
        c.close();

        return data;
    }

    public static Dealership setDealershipData(Cursor c)
    {
        int dealershipIdIndex = c.getColumnIndex("id");
        int dealershipId = c.getInt(dealershipIdIndex);

        int nameIndex = c.getColumnIndex("name");
        String dealershipName = c.getString(nameIndex);

        int dealerCodeIndex = c.getColumnIndex("dealercode");
        String dealerCode = c.getString(dealerCodeIndex);

        int lot1NameIndex = c.getColumnIndex("lot1name");
        String lot1Name = c.getString(lot1NameIndex);

        int lot2NameIndex = c.getColumnIndex("lot2name");
        String lot2Name = c.getString(lot2NameIndex);

        int lot3NameIndex = c.getColumnIndex("lot3name");
        String lot3Name = c.getString(lot3NameIndex);

        int lot4NameIndex = c.getColumnIndex("lot4name");
        String lot4Name = c.getString(lot4NameIndex);

        int lot5NameIndex = c.getColumnIndex("lot5name");
        String lot5Name = c.getString(lot5NameIndex);

        int lot6NameIndex = c.getColumnIndex("lot6name");
        String lot6Name = c.getString(lot6NameIndex);

        int lot7NameIndex = c.getColumnIndex("lot7name");
        String lot7Name = c.getString(lot7NameIndex);

        int lot8NameIndex = c.getColumnIndex("lot8name");
        String lot8Name = c.getString(lot8NameIndex);

        int lot9NameIndex = c.getColumnIndex("lot9name");
        String lot9Name = c.getString(lot9NameIndex);

        Dealership dealership = new Dealership();
        dealership.Id = dealershipId;
        dealership.Name = dealershipName;
        dealership.DealerCode = dealerCode;
        dealership.Lot1Name = lot1Name;
        dealership.Lot2Name = lot2Name;
        dealership.Lot3Name = lot3Name;
        dealership.Lot4Name = lot4Name;
        dealership.Lot5Name = lot5Name;
        dealership.Lot6Name = lot6Name;
        dealership.Lot7Name = lot7Name;
        dealership.Lot8Name = lot8Name;
        dealership.Lot9Name = lot9Name;

        return dealership;
    }
}
