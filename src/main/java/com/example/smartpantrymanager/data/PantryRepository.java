package com.example.smartpantrymanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantrymanager.database.Databasehelper;
import com.example.smartpantrymanager.models.PantryItem;

public class PantryRepository {

    private final Databasehelper databaseHelper;

    public PantryRepository(Context context) {
        databaseHelper = new Databasehelper(context);
    }

    public boolean addIngredient(PantryItem item) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Databasehelper.COLUMN_NAME, item.getName());
        values.put(Databasehelper.COLUMN_QUANTITY, item.getQuantity());
        values.put(Databasehelper.COLUMN_UNIT, item.getUnit());
        values.put(Databasehelper.COLUMN_EXPIRY_DATE,
                item.getExpiryDate());

        long result = db.insert(
                Databasehelper.TABLE_PANTRY,
                null,
                values
        );

        return result != -1;
    }

    public void close() {
        databaseHelper.close();
    }
}
