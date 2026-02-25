package com.sinfa.token.util;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FileUtil {

    // Ruta base para almacenar archivos subidos
    private static final String UPLOAD_BASE_PATH = "uploads/tokens/";

    // Extensiones permitidas
    private static final String[] ALLOWED_EXTENSIONS = {".pdf", ".jpg", ".jpeg", ".png", ".doc", ".docx", ".xls", ".xlsx"};

    // Tamaño máximo de archivo: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;


    public static String saveFile(InputStream fileInputStream, String originalFileName) {
        try {
            // Validar extensión
            if (!isValidFileExtension(originalFileName)) {
                System.err.println("✗ Extensión de archivo no permitida: " + originalFileName);
                return null;
            }

            // Crear directorio si no existe
            File uploadDir = new File(UPLOAD_BASE_PATH);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generar nombre único para el archivo
            String uniqueFileName = generateUniqueFileName(originalFileName);
            String filePath = UPLOAD_BASE_PATH + uniqueFileName;

            // Guardar archivo
            Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("✓ Archivo guardado: " + filePath);
            return uniqueFileName;

        } catch (IOException e) {
            System.err.println("✗ Error al guardar archivo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public static boolean deleteFile(String fileName) {
        try {
            if (fileName == null || fileName.isEmpty()) {
                return false;
            }

            File file = new File(UPLOAD_BASE_PATH + fileName);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("✓ Archivo eliminado: " + fileName);
                }
                return deleted;
            }
            return false;

        } catch (Exception e) {
            System.err.println("✗ Error al eliminar archivo: " + e.getMessage());
            return false;
        }
    }


    public static boolean fileExists(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        File file = new File(UPLOAD_BASE_PATH + fileName);
        return file.exists();
    }


    public static String getFilePath(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return UPLOAD_BASE_PATH + fileName;
    }


    public static String generateUniqueFileName(String originalFileName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String extension = getFileExtension(originalFileName);
        String randomId = String.valueOf(System.nanoTime());
        return "TOKEN_" + timestamp + "_" + randomId + extension;
    }


    public static boolean isValidExtension(String fileName) {
        return isValidFileExtension(fileName);
    }


    private static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }


    private static boolean isValidFileExtension(String fileName) {
        String extension = getFileExtension(fileName);
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isValidFileSize(long fileSize) {
        return fileSize <= MAX_FILE_SIZE;
    }


    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }


    public static String getContentType(String fileName) {
        String extension = getFileExtension(fileName);
        switch (extension) {
            case ".pdf":
                return "application/pdf";
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default:
                return "application/octet-stream";
        }
    }
}
