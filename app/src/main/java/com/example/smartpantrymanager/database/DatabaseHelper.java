package com.example.smartpantrymanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Naming database
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "ingredient_name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    private static final String CREATE_PANTRY_TABLE =
        "CREATE TABLE " + TABLE_PANTRY + "(" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_NAME + " TEXT NOT NULL, " +
        COLUMN_QUANTITY + " REAL NOT NULL, " +
        COLUMN_UNIT + " TEXT NOT NULL, " +
        COLUMN_EXPIRY_DATE + " TEXT" +
        ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table creation
        db.execSQL(CREATE_PANTRY_TABLE);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion, int newVersion) {
        // Database upgrades and handling
    }

}