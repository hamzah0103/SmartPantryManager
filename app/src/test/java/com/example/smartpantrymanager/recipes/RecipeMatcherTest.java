package com.example.smartpantrymanager.recipes;
import com.example.smartpantrymanager.recipes.Recipe;
import com.example.smartpantrymanager.recipes.RecipeMatcher;
import com.example.smartpantrymanager.recipes.RecipeIngredient;

import com.example.smartpantrymanager.models.PantryItem;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecipeMatcherTest {

    @Test
    public void sufficientIngredientsShouldMatch() {
        RecipeMatcher matcher = new RecipeMatcher(Arrays.asList(
                new PantryItem(1, "Eggs", 4, "pieces", null),
                new PantryItem(2, "Oil", 20, "ml", null)
        ));

        Recipe recipe = new Recipe(
                1, "Egg Recipe", "Eggs and oil",
                Arrays.asList(
                        new RecipeIngredient("eggs", 2, "pieces"),
                        new RecipeIngredient("oil", 10, "ml")
                ),
                Collections.singletonList("Cook the eggs.")
        );

        assertTrue(matcher.canMakeRecipe(recipe));
    }

    @Test
    public void missingIngredientShouldRejectRecipe() {
        RecipeMatcher matcher = new RecipeMatcher(Collections.singletonList(
                new PantryItem(1, "Tomatoes", 2, "pieces", null)
        ));

        Recipe recipe = new Recipe(
                2, "Pasta Recipe", "Pasta and tomatoes",
                Arrays.asList(
                        new RecipeIngredient("pasta", 100, "g"),
                        new RecipeIngredient("tomatoes", 1, "piece")
                ),
                Collections.singletonList("Cook the pasta.")
        );

        assertFalse(matcher.canMakeRecipe(recipe));
    }

    @Test
    public void insufficientQuantityShouldRejectRecipe() {
        RecipeMatcher matcher = new RecipeMatcher(Collections.singletonList(
                new PantryItem(1, "Eggs", 1, "pieces", null)
        ));

        Recipe recipe = new Recipe(
                3, "Egg Recipe", "Requires two eggs",
                Collections.singletonList(
                        new RecipeIngredient("eggs", 2, "pieces")
                ),
                Collections.singletonList("Cook the eggs.")
        );

        assertFalse(matcher.canMakeRecipe(recipe));
    }

    @Test
    public void compatibleUnitsShouldConvertCorrectly() {
        RecipeMatcher matcher = new RecipeMatcher(Collections.singletonList(
                new PantryItem(1, "Flour", 1, "kg", null)
        ));

        Recipe recipe = new Recipe(
                4, "Flour Recipe", "Requires 500 grams",
                Collections.singletonList(
                        new RecipeIngredient("flour", 500, "g")
                ),
                Collections.singletonList("Use the flour.")
        );

        assertTrue(matcher.canMakeRecipe(recipe));
    }
}