package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.recipes.Recipe;
import com.example.smartpantrymanager.recipes.RecipeRepository;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.data.PantryRepository;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private PantryRepository pantryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView tvRecipeName = findViewById(R.id.tvRecipeName);
        TextView tvRecipeDescription = findViewById(R.id.tvRecipeDescription);
        TextView tvRecipeIngredients = findViewById(R.id.tvRecipeIngredients);
        TextView tvRecipeInstructions = findViewById(R.id.tvRecipeInstructions);
        Button btnBackToRecipes = findViewById(R.id.btnBackToRecipes);

        btnBackToRecipes.setOnClickListener(v -> finish());

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);

        RecipeRepository recipeRepository = new RecipeRepository(this);
        Recipe recipe = recipeRepository.getRecipeById(recipeId);

        if (recipe == null) {
            Toast.makeText(
                    this,
                    "Recipe could not be found.",
                    Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }

        tvRecipeName.setText(recipe.getName());
        tvRecipeDescription.setText(recipe.getDescription());


        SharedPreferences preferences = getSharedPreferences(
                "smart_pantry_settings",
                Context.MODE_PRIVATE
        );

        String unitPreference = preferences.getString(
                "unit_preference",
                "metric"
        );

        StringBuilder ingredientsText = new StringBuilder();

        for (com.example.smartpantrymanager.recipes.RecipeIngredient ingredient
                : recipe.getIngredients()) {

            String displayQuantity = UnitConverter.convertForDisplay(
                    ingredient.getQuantity(),
                    ingredient.getUnit(),
                    unitPreference
            );

            ingredientsText.append("• ")
                    .append(ingredient.getName())
                    .append(" — ")
                    .append(displayQuantity)
                    .append("\n");
        }

        tvRecipeIngredients.setText(ingredientsText.toString());


        StringBuilder instructionsText = new StringBuilder();

        List<String> instructions = recipe.getInstructions();

        for (int i = 0; i < instructions.size(); i++) {
            instructionsText.append(i + 1)
                    .append(". ")
                    .append(instructions.get(i))
                    .append("\n\n");
        }

        tvRecipeInstructions.setText(instructionsText.toString());
    }
}