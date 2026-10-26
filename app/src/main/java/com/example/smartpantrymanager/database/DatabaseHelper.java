package com.example.smartpantrymanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.smartpantrymanager.recipes.RecipeRepository;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database information
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 3;

    // Existing pantry table
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "ingredient_name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    // Recipes table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RECIPE_NAME = "recipe_name";
    public static final String COLUMN_RECIPE_DESCRIPTION = "description";

    // Recipe ingredients table
    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";
    public static final String COLUMN_RECIPE_INGREDIENT_ID =
            "recipe_ingredient_id";
    public static final String COLUMN_INGREDIENT_NAME =
            "ingredient_name";
    public static final String COLUMN_RECIPE_QUANTITY =
            "quantity";
    public static final String COLUMN_RECIPE_UNIT =
            "unit";

    // Recipe instructions table
    public static final String TABLE_RECIPE_INSTRUCTIONS =
            "recipe_instructions";
    public static final String COLUMN_INSTRUCTION_ID =
            "instruction_id";
    public static final String COLUMN_INSTRUCTION_TEXT =
            "instruction_text";
    public static final String COLUMN_INSTRUCTION_ORDER =
            "instruction_order";

    // Create the existing pantry table
    private static final String CREATE_PANTRY_TABLE =
            "CREATE TABLE " + TABLE_PANTRY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_QUANTITY + " REAL NOT NULL, " +
                    COLUMN_UNIT + " TEXT NOT NULL, " +
                    COLUMN_EXPIRY_DATE + " TEXT" +
                    ")";

    // Create the recipes table
    private static final String CREATE_RECIPES_TABLE =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                    COLUMN_RECIPE_DESCRIPTION + " TEXT NOT NULL" +
                    ")";

    // Create the recipe ingredients table
    private static final String CREATE_RECIPE_INGREDIENTS_TABLE =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                    COLUMN_RECIPE_INGREDIENT_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                    COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
                    COLUMN_RECIPE_QUANTITY + " REAL NOT NULL, " +
                    COLUMN_RECIPE_UNIT + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_RECIPE_ID + ") REFERENCES " +
                    TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ") " +
                    "ON DELETE CASCADE" +
                    ")";

    // Create the recipe instructions table
    private static final String CREATE_RECIPE_INSTRUCTIONS_TABLE =
            "CREATE TABLE " + TABLE_RECIPE_INSTRUCTIONS + " (" +
                    COLUMN_INSTRUCTION_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                    COLUMN_INSTRUCTION_TEXT + " TEXT NOT NULL, " +
                    COLUMN_INSTRUCTION_ORDER + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_RECIPE_ID + ") REFERENCES " +
                    TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ") " +
                    "ON DELETE CASCADE" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create the pantry table
        db.execSQL(CREATE_PANTRY_TABLE);

        // Create recipe tables
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);
        db.execSQL(CREATE_RECIPE_INSTRUCTIONS_TABLE);

        // Inserting the initial recipes
        RecipeRepository.seedRecipes(db);
    }


    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Upgrade database version without deleting pantry data.
        if (oldVersion < 2) {
            db.execSQL(CREATE_RECIPES_TABLE);
            db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);
            db.execSQL(CREATE_RECIPE_INSTRUCTIONS_TABLE);
        }

        // Seed recipes if the database has no recipes yet.
        if (oldVersion < 3) {
            Cursor cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM recipes",
                    null
            );

            try {
                if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                    RecipeRepository.seedRecipes(db);
                }
            } finally {
                cursor.close();
            }
        }
    }

}