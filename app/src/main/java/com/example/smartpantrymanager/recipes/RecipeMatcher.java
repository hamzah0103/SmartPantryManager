package com.example.smartpantrymanager.recipes;

import com.example.smartpantrymanager.models.PantryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RecipeMatcher {

    private final List<PantryItem> pantryItems;

    public RecipeMatcher(List<PantryItem> pantryItems) {
        this.pantryItems = new ArrayList<>(pantryItems);
    }

    public List<Recipe> getAvailableRecipes(List<Recipe> recipes) {

        List<Recipe> availableRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {

            if (canMakeRecipe(recipe)) {
                availableRecipes.add(recipe);
            }
        }

        return availableRecipes;
    }

    public boolean canMakeRecipe(Recipe recipe) {

        for (RecipeIngredient required : recipe.getIngredients()) {

            double availableQuantity = getAvailableQuantity(
                    required.getName(),
                    required.getUnit()
            );

            if (availableQuantity < required.getQuantity()) {
                return false;
            }
        }

        return true;
    }

    private double getAvailableQuantity(
            String requiredName,
            String requiredUnit) {

        double totalQuantity = 0;

        for (PantryItem pantryItem : pantryItems) {

            if (sameIngredient(
                    pantryItem.getName(),
                    requiredName)) {

                double convertedQuantity = convertQuantity(
                        pantryItem.getQuantity(),
                        pantryItem.getUnit(),
                        requiredUnit
                );

                if (convertedQuantity >= 0) {
                    totalQuantity += convertedQuantity;
                }
            }
        }

        return totalQuantity;
    }

    private boolean sameIngredient(String first, String second) {

        String normalFirst = normaliseName(first);
        String normalSecond = normaliseName(second);

        return normalFirst.equals(normalSecond);
    }

    private String normaliseName(String name) {

        String normalised = name.trim()
                .toLowerCase(Locale.ROOT);

        if (normalised.endsWith("ies")
                && normalised.length() > 3) {

            normalised = normalised.substring(
                    0, normalised.length() - 3) + "y";

        } else if (normalised.endsWith("s")
                && !normalised.endsWith("ss")
                && normalised.length() > 1) {

            normalised = normalised.substring(
                    0, normalised.length() - 1);
        }

        return normalised;
    }

    private double convertQuantity(
            double quantity,
            String pantryUnit,
            String requiredUnit) {

        String from = normaliseUnit(pantryUnit);
        String to = normaliseUnit(requiredUnit);

        if (from.equals(to)) {
            return quantity;
        }

        // Weight conversions: grams and kilograms.
        if (from.equals("g") && to.equals("kg")) {
            return quantity / 1000.0;
        }

        if (from.equals("kg") && to.equals("g")) {
            return quantity * 1000.0;
        }

        // Volume conversions: millilitres and litres.
        if (from.equals("ml") && to.equals("l")) {
            return quantity / 1000.0;
        }

        if (from.equals("l") && to.equals("ml")) {
            return quantity * 1000.0;
        }

        // Incompatible units must never count as available.
        return -1;
    }

    private String normaliseUnit(String unit) {

        String normalised = unit.trim()
                .toLowerCase(Locale.ROOT);

        switch (normalised) {

            case "gram":
            case "grams":
            case "g":
                return "g";

            case "kilogram":
            case "kilograms":
            case "kgs":
            case "kg":
                return "kg";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "ml":
                return "ml";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "l":
                return "l";

            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "pieces";

            case "slice":
            case "slices":
                return "slices";

            case "clove":
            case "cloves":
                return "cloves";

            default:
                return normalised;
        }
    }
}