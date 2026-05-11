package com.juanoff.util;

import java.util.function.Function;

public class NumericValueParser {
    public static double parseDouble(String input, String fieldName) {
        return parse(input, fieldName, Double::parseDouble, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public static double parseDouble(String input, String fieldName, double min, double max) {
        return parse(input, fieldName, Double::parseDouble, min, max);
    }

    public static int parseInt(String input, String fieldName) {
        return parse(input, fieldName, Integer::parseInt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static int parseInt(String input, String fieldName, int min, int max) {
        return parse(input, fieldName, Integer::parseInt, min, max);
    }

    public static <T extends Number & Comparable<T>> T parse(
            String input,
            String fieldName,
            Function<String, T> parser,
            T min,
            T max
    ) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        try {
            T value = parser.apply(input.trim());
            if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
                throw new IllegalArgumentException(
                        String.format("%s must be between %s and %s", fieldName, min, max)
                );
            }
            if (value instanceof Double d && (d.isNaN() || d.isInfinite())) {
                throw new IllegalArgumentException("Value cannot be NaN or Infinity");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid %s format: '%s'. Expected a number.", fieldName, input)
            );
        }
    }
}
