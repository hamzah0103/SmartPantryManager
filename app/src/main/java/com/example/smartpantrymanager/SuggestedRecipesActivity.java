package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.data.PantryRepository;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.recipes.Recipe;
import com.example.smartpantrymanager.recipes.RecipeMatcher;
import com.example.smartpantrymanager.recipes.RecipeRepository;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private PantryRepository pantryRepository;
    private LinearLayout recipeContainer;
    private TextView tvRecipeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        recipeContainer = findViewById(R.id.recipeContainer);
        tvRecipeMessage = findViewById(R.id.tvRecipeMessage);

        Button btnBackToHome = findViewById(R.id.btnBackToHome);

        btnBackToHome.setOnClickListener(v -> finish());

        pantryRepository = new PantryRepository(this);

        displaySuggestedRecipes();
    }

    private void displaySuggestedRecipes() {

        List<PantryItem> pantryItems =
                pantryRepository.getAllIngredients();

        RecipeRepository recipeRepository = new RecipeRepository(this);

        android.widget.Toast.makeText(
                this,
                "Recipes loaded: "
                        + recipeRepository.getAllRecipes().size(),
                android.widget.Toast.LENGTH_LONG
        ).show();

        RecipeMatcher recipeMatcher =
                new RecipeMatcher(pantryItems);

        List<Recipe> availableRecipes =
                recipeMatcher.getAvailableRecipes(
                        recipeRepository.getAllRecipes()
                );

        recipeContainer.removeAllViews();

        if (availableRecipes.isEmpty()) {
            tvRecipeMessage.setVisibility(View.VISIBLE);
            tvRecipeMessage.setText(
                    "No recipes can be made with your current ingredients. " +
                            "Try adding more ingredients to your pantry."
            );
            return;
        }

        tvRecipeMessage.setVisibility(View.GONE);

        for (Recipe recipe : availableRecipes) {

            Button recipeButton = new Button(this);
            recipeButton.setText(recipe.getName());

            recipeButton.setOnClickListener(v -> {
                Intent intent = new Intent(
                        SuggestedRecipesActivity.this,
                        RecipeDetailActivity.class
                );

                intent.putExtra(
                        RecipeDetailActivity.EXTRA_RECIPE_ID,
                        recipe.getId()
                );

                startActivity(intent);
            });


            recipeContainer.addView(recipeButton);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryRepository != null) {
            pantryRepository.close();
        }
    }
}