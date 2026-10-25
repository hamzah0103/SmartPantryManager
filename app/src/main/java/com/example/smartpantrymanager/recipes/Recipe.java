package com.example.smartpantrymanager.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Recipe {

    private final int id;
    private final String name;
    private final String description;
    private final List<RecipeIngredient> ingredients;
    private final List<String> instructions;

    public Recipe(
            int id,
            String name,
            String description,
            List<RecipeIngredient> ingredients,
            List<String> instructions) {

        this.id = id;
        this.name = name;
        this.description = description;

        this.ingredients = new ArrayList<>(ingredients);
        this.instructions = new ArrayList<>(instructions);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<RecipeIngredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public List<String> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }
}