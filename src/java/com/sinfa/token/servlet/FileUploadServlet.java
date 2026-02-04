package com.sinfa.token.servlet;

import com.sinfa.token.util.FileUtil;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload", "/download"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 5,        // 5MB
    maxRequestSize = 1024 * 1024 * 10     // 10MB
)
public class FileUploadServlet extends HttpServlet {
    
    private static final String UPLOAD_DIR = "uploads/tokens";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.getWriter().write("{\"error\":\"Sesión expirada\"}");
            return;
        }
        
        try {
            // Obtener el archivo
            Part filePart = request.getPart("file");
            
            if (filePart == null || filePart.getSize() == 0) {
                response.getWriter().write("{\"error\":\"No se recibió ningún archivo\"}");
                return;
            }
            
            String fileName = getFileName(filePart);
            
            // Validar extensión
            if (!FileUtil.isValidExtension(fileName)) {
                response.getWriter().write("{\"error\":\"Solo se permiten archivos PDF, JPG, PNG\"}");
                return;
            }
            
            // Validar tamaño
            if (!FileUtil.isValidFileSize(filePart.getSize())) {
                response.getWriter().write("{\"error\":\"El archivo no debe superar los 5MB\"}");
                return;
            }
            
            // Crear directorio si no existe
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Generar nombre único
            String uniqueFileName = FileUtil.generateUniqueFileName(fileName);
            String filePath = uploadPath + File.separator + uniqueFileName;
            
            // Guardar archivo
            filePart.write(filePath);
            
            System.out.println("✓ Archivo guardado: " + uniqueFileName);
            
            // Retornar respuesta JSON
            String json = String.format(
                "{\"success\":true,\"fileName\":\"%s\",\"originalName\":\"%s\",\"size\":%d}",
                uniqueFileName,
                fileName,
                filePart.getSize()
            );
            
            response.getWriter().write(json);
            
        } catch (Exception e) {
            System.err.println("✗ Error al subir archivo: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("{\"error\":\"Error al subir archivo: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String fileName = request.getParameter("file");
        
        if (fileName == null || fileName.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de archivo requerido");
            return;
        }
        
        // Validar que el archivo existe
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File file = new File(uploadPath + File.separator + fileName);
        
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado");
            return;
        }
        
        // Determinar tipo de contenido
        String mimeType = FileUtil.getContentType(fileName);
        response.setContentType(mimeType);
        
        // Si es PDF, mostrarlo inline; si es imagen, también inline
        if (mimeType.equals("application/pdf") || mimeType.startsWith("image/")) {
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        }
        
        // Enviar archivo
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            
            System.out.println("✓ Archivo descargado: " + fileName);
            
        } catch (IOException e) {
            System.err.println("✗ Error al descargar archivo: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtener nombre del archivo del Part
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        
        return "";
    }
}