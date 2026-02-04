package com.sinfa.token.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Formatear fecha a string
     * @param date
     * @return fecha formateada (dd/MM/yyyy)
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Formatear fecha y hora a string
     * @param date
     * @return fecha y hora formateada
     */
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return DATETIME_FORMAT.format(date);
    }
    
    /**
     * Formatear fecha para SQL
     * @param date
     * @return fecha en formato SQL (yyyy-MM-dd)
     */
    public static String formatSqlDate(Date date) {
        if (date == null) return null;
        return SQL_DATE_FORMAT.format(date);
    }
    
    /**
     * Parsear string a fecha
     * @param dateString formato dd/MM/yyyy
     * @return Date o null si hay error
     */
    public static Date parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            System.err.println("Error al parsear fecha: " + dateString);
            return null;
        }
    }
    
    /**
     * Parsear string SQL a fecha
     * @param dateString formato yyyy-MM-dd
     * @return Date o null si hay error
     */
    public static Date parseSqlDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;
        try {
            return SQL_DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            System.err.println("Error al parsear fecha SQL: " + dateString);
            return null;
        }
    }
    
    /**
     * Obtener fecha actual
     * @return Date
     */
    public static Date getCurrentDate() {
        return new Date();
    }
    
    /**
     * Obtener fecha actual como SQL Date
     * @return java.sql.Date
     */
    public static java.sql.Date getCurrentSqlDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }
    
    /**
     * Obtener timestamp actual
     * @return java.sql.Timestamp
     */
    public static java.sql.Timestamp getCurrentTimestamp() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Validar formato de fecha
     * @param dateString
     * @return true si es válida
     */
    public static boolean isValidDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return false;
        try {
            DATE_FORMAT.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}