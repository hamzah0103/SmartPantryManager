package com.example.smartpantrymanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;

public class PantryRepository {

    private final DatabaseHelper databaseHelper;

    public PantryRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public boolean addIngredient(PantryItem item) {
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, item.getName());
            values.put(DatabaseHelper.COLUMN_QUANTITY, item.getQuantity());
            values.put(DatabaseHelper.COLUMN_UNIT, item.getUnit());
            values.put(DatabaseHelper.COLUMN_EXPIRY_DATE, item.getExpiryDate());

            long result = db.insert(
                    DatabaseHelper.TABLE_PANTRY,
                    null,
                    values
            );

            return result != -1;

        } catch (SQLiteException e) {
            return false;
        }
    }


    public List<PantryItem> getAllIngredients() {
        List<PantryItem> ingredients = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_NAME + " ASC"
        );

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY)
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UNIT)
                );

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPIRY_DATE)
                );

                PantryItem item = new PantryItem(
                        id, name, quantity, unit, expiryDate
                );

                ingredients.add(item);
            }
        } finally {
            cursor.close();
        }

        return ingredients;
    }

    public boolean deleteIngredient(int id) {
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            int rowsDeleted = db.delete(
                    DatabaseHelper.TABLE_PANTRY,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            return rowsDeleted > 0;

        } catch (SQLiteException e) {
            return false;
        }
    }


    public boolean updateIngredient(PantryItem item) {
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, item.getName());
            values.put(DatabaseHelper.COLUMN_QUANTITY, item.getQuantity());
            values.put(DatabaseHelper.COLUMN_UNIT, item.getUnit());
            values.put(DatabaseHelper.COLUMN_EXPIRY_DATE, item.getExpiryDate());

            int rowsUpdated = db.update(
                    DatabaseHelper.TABLE_PANTRY,
                    values,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(item.getId())}
            );

            return rowsUpdated > 0;

        } catch (SQLiteException e) {
            return false;
        }
    }


    public void close() {
        databaseHelper.close();
    }
}