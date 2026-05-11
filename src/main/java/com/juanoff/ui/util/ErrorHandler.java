package com.juanoff.ui.util;

public class ErrorHandler {
    public static String toUserMessage(Exception e) {
        if (e instanceof NumberFormatException) {
            return "Enter valid number";
        }

        if (e instanceof IndexOutOfBoundsException) {
            return e.getMessage();
        }
        
        if (e instanceof IllegalStateException) {
            return e.getMessage();
        }

        if (e instanceof IllegalArgumentException) {
            return e.getMessage();
        }

        return "Error occurred. Please try again.";
    }
}
