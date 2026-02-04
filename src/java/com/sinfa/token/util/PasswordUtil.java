package com.sinfa.token.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class PasswordUtil {
    
    /**
     * Generar hash SHA-256 de una contraseña
     * @param password
     * @return hash en formato hexadecimal
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al hashear contraseña: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verificar si una contraseña coincide con su hash
     * @param password
     * @param hashedPassword
     * @return true si coinciden
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        String hash = hashPassword(password);
        return hash != null && hash.equals(hashedPassword);
    }
    
    /**
     * Generar contraseña aleatoria
     * @param length
     * @return contraseña generada
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    
    /**
     * Validar fortaleza de contraseña
     * @param password
     * @return mensaje de error o null si es válida
     */
    public static String validatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "La contraseña no puede estar vacía";
        }
        
        if (password.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }
        
        if (!password.matches(".*[A-Z].*")) {
            return "La contraseña debe contener al menos una mayúscula";
        }
        
        if (!password.matches(".*[a-z].*")) {
            return "La contraseña debe contener al menos una minúscula";
        }
        
        if (!password.matches(".*[0-9].*")) {
            return "La contraseña debe contener al menos un número";
        }
        
        return null; // Contraseña válida
    }
}