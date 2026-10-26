
package com.example.smartpantrymanager;

import java.util.Locale;

public final class UnitConverter {

    private UnitConverter() {
    }

    public static String convertForDisplay(
            double quantity,
            String unit,
            String preference) {

        if (unit == null || unit.trim().isEmpty()) {
            return format(quantity) + " " + unit;
        }

        String normalizedUnit = unit.trim().toLowerCase(Locale.ROOT);
        boolean imperial = "imperial".equalsIgnoreCase(preference);

        if (imperial) {
            switch (normalizedUnit) {
                case "g":
                case "gram":
                case "grams":
                    return format(quantity / 453.59237) + " lb";

                case "kg":
                case "kilogram":
                case "kilograms":
                    return format(quantity * 2.20462262) + " lb";

                case "ml":
                case "milliliter":
                case "milliliters":
                case "millilitre":
                case "millilitres":
                    return format(quantity / 236.5882365) + " cups";

                case "l":
                case "liter":
                case "liters":
                case "litre":
                case "litres":
                    return format(quantity * 4.22675284) + " cups";

                default:
                    return format(quantity) + " " + unit;
            }
        } else {
            switch (normalizedUnit) {
                case "lb":
                case "lbs":
                case "pound":
                case "pounds":
                    return format(quantity * 453.59237) + " g";

                case "oz":
                case "ounce":
                case "ounces":
                    return format(quantity * 28.349523125) + " g";

                case "cup":
                case "cups":
                    return format(quantity * 236.5882365) + " ml";

                case "tbsp":
                case "tablespoon":
                case "tablespoons":
                    return format(quantity * 14.7867648) + " ml";

                case "tsp":
                case "teaspoon":
                case "teaspoons":
                    return format(quantity * 4.92892159) + " ml";

                default:
                    return format(quantity) + " " + unit;
            }
        }
    }

    private static String format(double quantity) {
        return String.format(Locale.getDefault(), "%.2f", quantity)
                .replaceAll("\\.?0+$", "");
    }
}
