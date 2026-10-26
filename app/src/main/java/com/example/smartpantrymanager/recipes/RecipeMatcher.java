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

        // Convert all supported weight units to grams.
        double quantityInGrams;

        switch (from) {
            case "g":
                quantityInGrams = quantity;
                break;
            case "kg":
                quantityInGrams = quantity * 1000.0;
                break;
            case "lb":
                quantityInGrams = quantity * 453.59237;
                break;
            case "oz":
                quantityInGrams = quantity * 28.349523125;
                break;
            default:
                quantityInGrams = -1;
        }

        if (quantityInGrams >= 0) {
            switch (to) {
                case "g":
                    return quantityInGrams;
                case "kg":
                    return quantityInGrams / 1000.0;
                case "lb":
                    return quantityInGrams / 453.59237;
                case "oz":
                    return quantityInGrams / 28.349523125;
            }
        }

        // Convert supported volume units to millilitres.
        double quantityInMl;

        switch (from) {
            case "ml":
                quantityInMl = quantity;
                break;
            case "l":
                quantityInMl = quantity * 1000.0;
                break;
            case "cup":
                quantityInMl = quantity * 236.5882365;
                break;
            case "tbsp":
                quantityInMl = quantity * 14.7867648;
                break;
            case "tsp":
                quantityInMl = quantity * 4.92892159;
                break;
            default:
                quantityInMl = -1;
        }

        if (quantityInMl >= 0) {
            switch (to) {
                case "ml":
                    return quantityInMl;
                case "l":
                    return quantityInMl / 1000.0;
                case "cup":
                    return quantityInMl / 236.5882365;
                case "tbsp":
                    return quantityInMl / 14.7867648;
                case "tsp":
                    return quantityInMl / 4.92892159;
            }
        }

        // Never compare incompatible units, such as grams and millilitres.
        return -1;
    }



    private String normaliseUnit(String unit) {

        String normalised = unit.trim().toLowerCase(Locale.ROOT);

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

            case "pound":
            case "pounds":
            case "lbs":
            case "lb":
                return "lb";

            case "ounce":
            case "ounces":
            case "oz":
                return "oz";

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

            case "cup":
            case "cups":
                return "cup";

            case "tablespoon":
            case "tablespoons":
            case "tbsp":
                return "tbsp";

            case "teaspoon":
            case "teaspoons":
            case "tsp":
                return "tsp";

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