package com.sinfa.token.util;


public class ValidationUtil {
    
    /**
     * Validar DNI peruano (8 dígitos)
     * @param dni
     * @return true si es válido
     */
    public static boolean isValidDNI(String dni) {
        if (dni == null || dni.isEmpty()) {
            return false;
        }
        return dni.matches("^[0-9]{8}$");
    }
    
    /**
     * Validar email
     * @param email
     * @return true si es válido
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Validar que un string no esté vacío
     * @param str
     * @return true si no está vacío
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validar número entero
     * @param str
     * @return true si es un número válido
     */
    public static boolean isValidInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Sanitizar input para prevenir XSS
     * @param input
     * @return input sanitizado
     */
    public static String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("/", "&#x2F;");
    }
}