package com.example.smartpantrymanager.recipes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantrymanager.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecipeRepository {

    private final List<Recipe> recipes;

    public RecipeRepository(Context context) {
        recipes = loadRecipesFromDatabase(context);
    }

    public List<Recipe> getAllRecipes() {
        return Collections.unmodifiableList(recipes);
    }

    public Recipe getRecipeById(int id) {
        for (Recipe recipe : recipes) {
            if (recipe.getId() == id) {
                return recipe;
            }
        }

        return null;
    }

    private List<Recipe> loadRecipesFromDatabase(Context context) {
        List<Recipe> loadedRecipes = new ArrayList<>();

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor recipeCursor = db.query(
                DatabaseHelper.TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_RECIPE_ID + " ASC"
        );

        try {
            while (recipeCursor.moveToNext()) {
                int recipeId = recipeCursor.getInt(
                        recipeCursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_RECIPE_ID
                        )
                );

                String name = recipeCursor.getString(
                        recipeCursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_RECIPE_NAME
                        )
                );

                String description = recipeCursor.getString(
                        recipeCursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_RECIPE_DESCRIPTION
                        )
                );

                List<RecipeIngredient> ingredients =
                        loadIngredients(db, recipeId);

                List<String> instructions =
                        loadInstructions(db, recipeId);

                loadedRecipes.add(
                        new Recipe(
                                recipeId,
                                name,
                                description,
                                ingredients,
                                instructions
                        )
                );
            }
        } finally {
            recipeCursor.close();
            helper.close();
        }

        return loadedRecipes;
    }

    private List<RecipeIngredient> loadIngredients(
            SQLiteDatabase db,
            int recipeId
    ) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                null,
                DatabaseHelper.COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                DatabaseHelper.COLUMN_RECIPE_INGREDIENT_ID + " ASC"
        );

        try {
            while (cursor.moveToNext()) {
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_INGREDIENT_NAME
                        )
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_RECIPE_QUANTITY
                        )
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_RECIPE_UNIT
                        )
                );

                ingredients.add(
                        new RecipeIngredient(name, quantity, unit)
                );
            }
        } finally {
            cursor.close();
        }

        return ingredients;
    }

    private List<String> loadInstructions(
            SQLiteDatabase db,
            int recipeId
    ) {
        List<String> instructions = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_RECIPE_INSTRUCTIONS,
                null,
                DatabaseHelper.COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                DatabaseHelper.COLUMN_INSTRUCTION_ORDER + " ASC"
        );

        try {
            while (cursor.moveToNext()) {
                instructions.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_INSTRUCTION_TEXT
                                )
                        )
                );
            }
        } finally {
            cursor.close();
        }

        return instructions;
    }

    // Changed to private static - so seeder accesses the original recipe
    // without making another repository
    private static List<Recipe> createRecipes() {

        List<Recipe> list = new ArrayList<>();

        list.add(recipe(1, "Egg and Tomato Scramble",
                "A quick, simple breakfast.",
                ingredients(
                        "eggs", 2, "pieces",
                        "tomatoes", 1, "piece",
                        "oil", 10, "ml"),
                "Chop the tomato.",
                "Heat the oil in a pan.",
                "Add the tomato and cook briefly.",
                "Add the eggs and scramble until cooked."));

        list.add(recipe(2, "Tomato Pasta",
                "A simple pasta dish with tomato.",
                ingredients(
                        "pasta", 150, "g",
                        "tomatoes", 2, "pieces",
                        "oil", 10, "ml"),
                "Cook the pasta according to its instructions.",
                "Chop the tomatoes.",
                "Heat the oil and cook the tomatoes.",
                "Mix the cooked pasta with the tomato sauce."));

        list.add(recipe(3, "Potato Wedges",
                "Oven-baked seasoned potato wedges.",
                ingredients(
                        "potatoes", 300, "g",
                        "oil", 15, "ml",
                        "salt", 2, "g"),
                "Wash and cut the potatoes into wedges.",
                "Toss the wedges with oil and salt.",
                "Bake at 200 degrees Celsius until golden and tender."));

        list.add(recipe(4, "Cheese Sandwich",
                "A quick toasted cheese sandwich.",
                ingredients(
                        "bread", 2, "slices",
                        "cheese", 40, "g"),
                "Place the cheese between the bread slices.",
                "Toast in a pan or sandwich maker until golden and melted."));

        list.add(recipe(5, "Vegetable Fried Rice",
                "Rice stir-fried with vegetables and egg.",
                ingredients(
                        "rice", 200, "g",
                        "carrots", 1, "piece",
                        "eggs", 2, "pieces",
                        "oil", 15, "ml"),
                "Cook the rice and allow it to cool.",
                "Chop the carrot.",
                "Heat the oil and cook the carrot.",
                "Add the eggs and stir until cooked.",
                "Add the rice and stir-fry until heated through."));

        list.add(recipe(6, "Garlic Pasta",
                "Pasta with a simple garlic and oil sauce.",
                ingredients(
                        "pasta", 150, "g",
                        "garlic", 2, "cloves",
                        "oil", 20, "ml"),
                "Cook the pasta according to its instructions.",
                "Finely chop the garlic.",
                "Gently heat the oil and garlic.",
                "Toss the cooked pasta in the garlic oil."));

        list.add(recipe(7, "Mashed Potatoes",
                "Creamy mashed potatoes.",
                ingredients(
                        "potatoes", 300, "g",
                        "milk", 50, "ml",
                        "butter", 20, "g"),
                "Peel and cut the potatoes.",
                "Boil until soft.",
                "Drain and mash with milk and butter."));

        list.add(recipe(8, "Pancakes",
                "Simple homemade pancakes.",
                ingredients(
                        "flour", 150, "g",
                        "milk", 200, "ml",
                        "eggs", 1, "piece"),
                "Mix the flour, milk and egg into a smooth batter.",
                "Heat a lightly greased pan.",
                "Pour in some batter and cook both sides until golden."));

        list.add(recipe(9, "Cheese Omelette",
                "An omelette filled with melted cheese.",
                ingredients(
                        "eggs", 2, "pieces",
                        "cheese", 30, "g",
                        "oil", 5, "ml"),
                "Beat the eggs.",
                "Heat the oil in a pan.",
                "Pour in the eggs and cook gently.",
                "Add the cheese, fold the omelette and cook until set."));

        list.add(recipe(10, "Tomato Salad",
                "A fresh and simple tomato salad.",
                ingredients(
                        "tomatoes", 2, "pieces",
                        "onions", 1, "piece"),
                "Wash and chop the tomatoes.",
                "Peel and slice the onion.",
                "Mix the ingredients together."));

        list.add(recipe(11, "Banana Pancakes",
                "Soft pancakes made with banana.",
                ingredients(
                        "bananas", 2, "pieces",
                        "eggs", 2, "pieces",
                        "flour", 50, "g"),
                "Mash the bananas.",
                "Mix in the eggs and flour.",
                "Cook small portions in a lightly greased pan."));

        list.add(recipe(12, "Carrot Soup",
                "A warming carrot soup.",
                ingredients(
                        "carrots", 250, "g",
                        "onions", 1, "piece",
                        "water", 500, "ml"),
                "Chop the carrots and onion.",
                "Place them in a pot with the water.",
                "Simmer until soft.",
                "Blend until smooth, if a blender is available."));

        list.add(recipe(13, "Egg Fried Rice",
                "A simple rice dish with scrambled eggs.",
                ingredients(
                        "rice", 200, "g",
                        "eggs", 2, "pieces",
                        "oil", 10, "ml"),
                "Cook the rice and allow it to cool.",
                "Scramble the eggs in a pan with oil.",
                "Add the rice and stir-fry until hot."));

        list.add(recipe(14, "Garlic Mashed Potatoes",
                "Mashed potatoes with garlic flavour.",
                ingredients(
                        "potatoes", 300, "g",
                        "garlic", 2, "cloves",
                        "butter", 20, "g"),
                "Boil the chopped potatoes until soft.",
                "Mash the potatoes with butter.",
                "Add finely chopped garlic and mix thoroughly."));

        list.add(recipe(15, "Cheese and Tomato Sandwich",
                "A sandwich with cheese and fresh tomato.",
                ingredients(
                        "bread", 2, "slices",
                        "cheese", 30, "g",
                        "tomatoes", 1, "piece"),
                "Slice the tomato.",
                "Layer the tomato and cheese on the bread.",
                "Serve fresh or toast until the cheese melts."));

        list.add(recipe(16, "Boiled Eggs on Toast",
                "Boiled eggs served with toast.",
                ingredients(
                        "eggs", 2, "pieces",
                        "bread", 2, "slices"),
                "Boil the eggs until cooked to your preference.",
                "Toast the bread.",
                "Peel and slice the eggs, then serve on the toast."));

        list.add(recipe(17, "Carrot and Potato Mash",
                "A simple mash using two vegetables.",
                ingredients(
                        "carrots", 100, "g",
                        "potatoes", 200, "g",
                        "butter", 15, "g"),
                "Peel and chop the vegetables.",
                "Boil until soft.",
                "Drain, mash with butter and serve."));

        list.add(recipe(18, "Onion Omelette",
                "An omelette with cooked onion.",
                ingredients(
                        "eggs", 2, "pieces",
                        "onions", 1, "piece",
                        "oil", 10, "ml"),
                "Slice the onion.",
                "Cook the onion in oil until softened.",
                "Beat the eggs, add them to the pan and cook until set."));

        list.add(recipe(19, "Simple Milk Porridge",
                "A warm breakfast made with oats and milk.",
                ingredients(
                        "oats", 50, "g",
                        "milk", 250, "ml"),
                "Combine the oats and milk in a saucepan.",
                "Cook over low heat, stirring regularly, until thickened."));

        list.add(recipe(20, "Flour Flatbread",
                "A simple flatbread made with flour and water.",
                ingredients(
                        "flour", 150, "g",
                        "water", 100, "ml",
                        "salt", 2, "g"),
                "Mix the flour, water and salt into a dough.",
                "Knead until smooth.",
                "Divide into small portions and roll flat.",
                "Cook in a hot pan until lightly browned on both sides."));

        return list;
    }

    // Changed to private static
    private static Recipe recipe(
            int id,
            String name,
            String description,
            List<RecipeIngredient> ingredients,
            String... instructions) {

        return new Recipe(
                id,
                name,
                description,
                ingredients,
                Arrays.asList(instructions));
    }

    // Changed to private static
    private static List<RecipeIngredient> ingredients(Object... values) {

        List<RecipeIngredient> result = new ArrayList<>();

        for (int i = 0; i < values.length; i += 3) {

            String name = (String) values[i];
            double quantity = ((Number) values[i + 1]).doubleValue();
            String unit = (String) values[i + 2];

            result.add(new RecipeIngredient(name, quantity, unit));
        }

        return result;
    }

    // Changed to private static
    public static void seedRecipes(SQLiteDatabase db) {

        List<Recipe> seedRecipeList = createRecipes();

        db.beginTransaction();

        try {
            for (Recipe recipe : seedRecipeList) {

                // Save the main recipe information
                ContentValues recipeValues = new ContentValues();
                recipeValues.put("recipe_id", recipe.getId());
                recipeValues.put("recipe_name", recipe.getName());
                recipeValues.put("description", recipe.getDescription());

                db.insertOrThrow(
                        "recipes",
                        null,
                        recipeValues
                );

                // Save the ingredients required for this recipe
                for (RecipeIngredient ingredient : recipe.getIngredients()) {

                    ContentValues ingredientValues = new ContentValues();
                    ingredientValues.put("recipe_id", recipe.getId());
                    ingredientValues.put(
                            "ingredient_name",
                            ingredient.getName()
                    );
                    ingredientValues.put(
                            "quantity",
                            ingredient.getQuantity()
                    );
                    ingredientValues.put("unit", ingredient.getUnit());

                    db.insertOrThrow(
                            "recipe_ingredients",
                            null,
                            ingredientValues
                    );
                }

                // Save each preparation step in its original order
                int stepNumber = 1;

                for (String instruction : recipe.getInstructions()) {

                    ContentValues instructionValues = new ContentValues();
                    instructionValues.put("recipe_id", recipe.getId());
                    instructionValues.put(
                            "instruction_text",
                            instruction
                    );
                    instructionValues.put(
                            "instruction_order",
                            stepNumber
                    );

                    db.insertOrThrow(
                            "recipe_instructions",
                            null,
                            instructionValues
                    );

                    stepNumber++;
                }
            }

            // Commit all inserts together
            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

}