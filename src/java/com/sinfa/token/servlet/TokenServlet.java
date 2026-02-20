package com.sinfa.token.servlet;

import com.sinfa.token.dao.*;
import com.sinfa.token.model.*;
import com.sinfa.token.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(name = "TokenServlet", urlPatterns = {"/tokens"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 5, // 5MB
        maxRequestSize = 1024 * 1024 * 10 // 10MB
)
public class TokenServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "C:/Users/informatica/Documents/token-proyecto/archivos";


    private TokenDAO tokenDAO;
    private EmpleadoDAO empleadoDAO;
    private DependenciaDAO dependenciaDAO;

    @Override
    public void init() throws ServletException {
        tokenDAO = new TokenDAO();
        empleadoDAO = new EmpleadoDAO();
        dependenciaDAO = new DependenciaDAO();
        System.out.println("✓ TokenServlet inicializado correctamente");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            if (action == null || "list".equals(action)) {
                listarTokens(request, response, usuario);
            } else if ("buscarEmpleado".equals(action)) {
                buscarEmpleadoPorDNI(request, response);
            } else if ("getToken".equals(action)) {
                obtenerTokenJSON(request, response);
            } else if ("delete".equals(action)) {
                eliminarToken(request, response, usuario);
            } else if ("restore".equals(action)) {
                restaurarToken(request, response, usuario);
            } else if ("buscar".equals(action)) {
                buscarTokens(request, response, usuario);
            } else if ("download".equals(action)) {
                descargarArchivo(request, response);
            } else {
                listarTokens(request, response, usuario);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            listarTokens(request, response, usuario);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            if ("crear".equals(action)) {
                crearToken(request, response, usuario);
            } else if ("actualizar".equals(action)) {
                // ✅ CORRECCIÓN: Agregado case para actualizar
                actualizarTokenAdmin(request, response, usuario);
            } else if ("confirmar1".equals(action)) {
                confirmarEntregaInicial(request, response, usuario);
            } else if ("confirmar2".equals(action)) {
                confirmarEntregaFinal(request, response, usuario);
            } else {
                System.out.println("⚠ Action no reconocida: " + action);
                response.sendRedirect("tokens");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            listarTokens(request, response, usuario);
        }
    }

    private void listarTokens(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        List<Token> tokens = new ArrayList<>();

        String perfil = usuario.getNombrePerfil(); // ← CLAVE
        System.out.println("========================================");
        System.out.println("LISTADO TOKENS - PERFIL: " + perfil);
        System.out.println("Usuario: " + usuario.getUsername());

        // ================= ADMIN GLOBAL =================
        if ("ADMINISTRADOR".equalsIgnoreCase(perfil)) {
            boolean mostrarOcultos = "1".equals(request.getParameter("mostrarOcultos"));

            System.out.println("ROL: ADMIN GLOBAL → VE TODO");
            tokens = tokenDAO.listarTodosAdmin(mostrarOcultos);
            request.setAttribute("mostrarOcultos", mostrarOcultos);

        } // ================= IMPLEMENTADOR NO ADMIN =================
        else if ("IMPLEMENTADOR".equalsIgnoreCase(perfil)) {

            System.out.println("ROL: IMPLEMENTADOR NO ADMIN");
            System.out.println("FILTRO CORRECTO: esttokcon IN (1,2)");
            //tokens = tokenDAO.listarParaImplementadorNoAdmin1();
            tokens = tokenDAO.buscar("", "", "", usuario.isAdmin(), usuario.getCempCoEmp());

            //tokens = tokenDAO.listarParaImplementadorNoAdmin2();
        } else {

            System.out.println("ROL NO AUTORIZADO");
            tokens = new ArrayList<>();
        }

        // ===== DEBUG FINAL (NO BORRAR) =====
        for (Token t : tokens) {
            Integer est = t.getEsttokcon();
            String estado
                    = est == null ? "CONFIRMACION PEND 1"
                            : est == 1 ? "CONFIRMACION PEND 2"
                                    : est == 2 ? "COMPLETO" : "DESCONOCIDO";

            System.out.println("Token DNI " + t.getNumdnitok() + " → " + estado);
        }

        request.setAttribute("tokens", tokens);
        request.setAttribute("totalTokens", tokens.size());
        Object ultimoTokenOcultadoId = request.getSession().getAttribute("ultimoTokenOcultadoId");
        request.setAttribute("ultimoTokenOcultadoId", ultimoTokenOcultadoId);
        request.setAttribute("dependencias", dependenciaDAO.listarActivas());

        request.getRequestDispatcher("tokens.jsp").forward(request, response);
    }

    private void crearToken(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        System.out.println("→ Creando nuevo token por: " + usuario.getUsername());

        // Obtener parámetros
        String dniUsuarioAsigna = request.getParameter("dniUsuarioAsigna");
        String dniUsuarioRecibe = request.getParameter("dniUsuarioRecibe");
        int unidadRegistra = Integer.parseInt(request.getParameter("unidadRegistra"));
        int accion = Integer.parseInt(request.getParameter("accion"));
        String fechaAccion = request.getParameter("fechaAccion");

        // Validar DNI
        if (!ValidationUtil.isValidDNI(dniUsuarioAsigna)) {
            request.setAttribute("error", "DNI inválido");
            listarTokens(request, response, usuario);
            return;
        }

        // Buscar empleado
        Empleado empAsigna = empleadoDAO.buscarPorDNI(dniUsuarioAsigna);
        if (empAsigna == null) {
            request.setAttribute("error", "Empleado no encontrado con DNI: " + dniUsuarioAsigna);
            listarTokens(request, response, usuario);
            return;
        }

        // Alertar si ya tiene registros
        int cantidadRegistros = empleadoDAO.contarRegistrosPorDNI(dniUsuarioAsigna);
        if (cantidadRegistros > 0) {
            request.setAttribute("warning",
                    "Ya se registró " + cantidadRegistros + " registro(s) para este DNI");
        }

        // Empleado que recibe (opcional)
        Empleado empRecibe = null;
        if (dniUsuarioRecibe != null && !dniUsuarioRecibe.trim().isEmpty()) {
            empRecibe = empleadoDAO.buscarPorDNI(dniUsuarioRecibe);
        }

        // Manejar archivo
        String nombreArchivo = null;
        Part filePart = request.getPart("docSustento");
        if (filePart != null && filePart.getSize() > 0) {
            nombreArchivo = guardarArchivo(filePart);
        }

        // Crear token
        Token token = new Token();
        token.setCodempreg(usuario.getCempCoEmp());
        int unidadDelUsuarioRegistrador = dependenciaDAO.obtenerCodigoDependenciaUsuario(usuario.getCempCoEmp());
        token.setUniregistra(unidadRegistra);
        token.setNumdnitok(dniUsuarioAsigna);
        token.setCodemptok(empAsigna.getCempCoEmp());
        token.setUniemptok(empAsigna.getCempCoDepend());
        token.setTipaccion(accion);
        token.setFecaccion(java.sql.Date.valueOf(fechaAccion));

        if (empRecibe != null) {
            token.setCodemprec(empRecibe.getCempCoEmp());
            token.setDniemprec(dniUsuarioRecibe);
        }

        token.setDocSustento(nombreArchivo);
        token.setEstado(1);
        token.setUsucre(usuario.getIdUsuario());

        // Guardar en BD
        int idNuevo = tokenDAO.insertar(token);

        if (idNuevo > 0) {
            request.setAttribute("success", "✓ Token asignado exitosamente (ID: " + idNuevo + ")");
            System.out.println("✓ Token creado ID:" + idNuevo + " por: " + usuario.getUsername());
        } else {
            request.setAttribute("error", "Error al guardar el token");
        }

        if (usuario.isAdmin()) {
            token.setUniregistra(unidadRegistra);
        } else {
            int unidadDelUsuario = dependenciaDAO.obtenerCodigoDependenciaUsuario(usuario.getCempCoEmp());
            token.setUniregistra(unidadDelUsuario);
        }

        listarTokens(request, response, usuario);
    }

    private void actualizarTokenAdmin(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        System.out.println("→ Actualizando token por ADMIN: " + usuario.getUsername());

        // Validar que sea ADMIN
        if (!usuario.isAdmin()) {
            request.setAttribute("error", "❌ Solo ADMIN puede editar tokens");
            listarTokens(request, response, usuario);
            return;
        }

        try {
            int idToken = Integer.parseInt(request.getParameter("idToken"));
            String dniUsuarioAsigna = request.getParameter("dniUsuarioAsigna");
            String dniUsuarioAsignaOriginal = request.getParameter("dniUsuarioAsignaOriginal");
            String dniUsuarioRecibe = request.getParameter("dniUsuarioRecibe");
            int unidadRegistra = Integer.parseInt(request.getParameter("unidadRegistra"));
            int accion = Integer.parseInt(request.getParameter("accion"));
            String fechaAccion = request.getParameter("fechaAccion");

            System.out.println("  - ID Token: " + idToken);
            System.out.println("  - DNI Asignado: " + dniUsuarioAsigna);
            System.out.println("  - Acción: " + accion);

            if ((dniUsuarioAsigna == null || dniUsuarioAsigna.trim().isEmpty())
                    && dniUsuarioAsignaOriginal != null && !dniUsuarioAsignaOriginal.trim().isEmpty()) {
                dniUsuarioAsigna = dniUsuarioAsignaOriginal.trim();
                System.out.println("  - DNI Asignado restaurado desde valor original: " + dniUsuarioAsigna);
            }

            // Validar DNI
            if (!ValidationUtil.isValidDNI(dniUsuarioAsigna)) {
                request.setAttribute("error", "DNI inválido");
                listarTokens(request, response, usuario);
                return;
            }

            // Buscar empleado
            Empleado empAsigna = empleadoDAO.buscarPorDNI(dniUsuarioAsigna);
            if (empAsigna == null) {
                request.setAttribute("error", "Empleado no encontrado con DNI: " + dniUsuarioAsigna);
                listarTokens(request, response, usuario);
                return;
            }

            // Obtener token actual
            Token token = tokenDAO.obtenerPorId(idToken);
            if (token == null) {
                request.setAttribute("error", "Token no encontrado");
                listarTokens(request, response, usuario);
                return;
            }

            // Actualizar datos básicos
            token.setUniregistra(unidadRegistra);
            token.setNumdnitok(dniUsuarioAsigna);
            token.setCodemptok(empAsigna.getCempCoEmp());
            token.setUniemptok(empAsigna.getCempCoDepend());
            token.setTipaccion(accion);
            token.setFecaccion(java.sql.Date.valueOf(fechaAccion));

            // Usuario que recibe (opcional)
            if (dniUsuarioRecibe != null && !dniUsuarioRecibe.trim().isEmpty() && ValidationUtil.isValidDNI(dniUsuarioRecibe)) {
                Empleado empRecibe = empleadoDAO.buscarPorDNI(dniUsuarioRecibe);
                if (empRecibe != null) {
                    token.setCodemprec(empRecibe.getCempCoEmp());
                    token.setDniemprec(dniUsuarioRecibe);
                }
            } else {
                token.setCodemprec(0);
                token.setDniemprec(null);
            }

            // Manejar archivo nuevo
            String archivoAnterior = token.getDocSustento();
            Part filePart = request.getPart("docSustento");

            if (filePart != null && filePart.getSize() > 0) {
                // Hay archivo nuevo
                System.out.println("  - Subiendo nuevo archivo...");
                String nombreArchivo = guardarArchivo(filePart);
                token.setDocSustento(nombreArchivo);

                // Eliminar archivo anterior si existe
                if (archivoAnterior != null && !archivoAnterior.isEmpty()) {
                    FileUtil.deleteFile(archivoAnterior);
                    System.out.println("  - Archivo anterior eliminado: " + archivoAnterior);
                }
            } else {
                System.out.println("  - Conservando archivo anterior");
            }
            // Si no hay archivo nuevo, conservar el anterior (ya está en el objeto token)

            token.setUsumod(usuario.getIdUsuario());

            // Actualizar en BD
            boolean ok = tokenDAO.actualizarAdmin(token);

            if (ok) {
                request.setAttribute("success", "✓ Token actualizado exitosamente");
                System.out.println("✓ Token " + idToken + " actualizado por: " + usuario.getUsername());
            } else {
                request.setAttribute("error", "Error al actualizar el token");
                System.out.println("✗ Error al actualizar token " + idToken);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        listarTokens(request, response, usuario);
    }

    private void confirmarEntregaInicial(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        System.out.println("→ Registrando primera confirmación por: " + usuario.getUsername());

        int idToken = Integer.parseInt(request.getParameter("idToken"));
        String dniConfirma = request.getParameter("dniConfirma");
        int tieneToken = Integer.parseInt(request.getParameter("tieneToken"));
        int estadoToken = Integer.parseInt(request.getParameter("estadoToken"));
        String fechaEntrega = request.getParameter("fechaEntrega");
        String observaciones = request.getParameter("observaciones");
        String unidadEntregaStr = request.getParameter("unidadEntrega");

        Empleado empConfirma = empleadoDAO.buscarPorDNI(dniConfirma);
        if (empConfirma == null) {
            request.setAttribute("error", "Empleado no encontrado");
            listarTokens(request, response, usuario);
            return;
        }

        String nombreArchivo = null;
        Part filePart = request.getPart("docSustentoEntrega");
        if (filePart != null && filePart.getSize() > 0) {
            nombreArchivo = guardarArchivo(filePart);
        }

        Token token = tokenDAO.obtenerPorId(idToken);
        if (token == null) {
            request.setAttribute("error", "Token no encontrado");
            listarTokens(request, response, usuario);
            return;
        }

        token.setCodempcon(usuario.getCempCoEmp());
        token.setUniconfirma(dependenciaDAO.obtenerCodigoDependenciaUsuario(usuario.getCempCoEmp()));
        token.setCodemptokcon(empConfirma.getCempCoEmp());
        token.setNumdnitokcon(dniConfirma);
        token.setUniemptokcon(empConfirma.getCempCoDepend());
        token.setFlgtokcon(tieneToken);
        token.setEsttokcon(1);

        if (estadoToken == 4 && unidadEntregaStr != null && !unidadEntregaStr.isEmpty()) {
            token.setUnienttokcon(Integer.parseInt(unidadEntregaStr));
        }

        token.setFecentcon(java.sql.Date.valueOf(fechaEntrega));
        token.setTxtobscon(observaciones);
        token.setDocSustentoEntrega(nombreArchivo);
        token.setUsumod(usuario.getIdUsuario());

        tokenDAO.actualizarConfirmacionInicial(token);

        response.sendRedirect("tokens");
    }

    private void confirmarEntregaFinal(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        System.out.println("→ Registrando segunda confirmación por: " + usuario.getUsername());

        int idToken = Integer.parseInt(request.getParameter("idToken"));

        Token token = tokenDAO.obtenerPorId(idToken);
        if (token == null) {
            request.setAttribute("error", "token no encontrado");
            listarTokens(request, response, usuario);
            return;
        }

        if (token.getCodempcon() != null && token.getCodempcon().equals(usuario.getCempCoEmp())) {
            request.setAttribute("error", "no se puede hacer confirmacion final la confirmacion inicial es para el mismo usuario");
            listarTokens(request, response, usuario);
            return;
        }

        String dniConfirma = request.getParameter("dniConfirma2");
        int tieneToken = Integer.parseInt(request.getParameter("tieneToken2"));
        int estadoToken = Integer.parseInt(request.getParameter("estadoToken2"));
        String fechaEntrega = request.getParameter("fechaEntrega2");
        String observaciones = request.getParameter("observaciones2");
        String unidadEntregaStr = request.getParameter("unidadEntrega2");

        Empleado empConfirma = empleadoDAO.buscarPorDNI(dniConfirma);
        if (empConfirma == null) {
            request.setAttribute("error", "Empleado no encontrado");
            listarTokens(request, response, usuario);
            return;
        }

        // Archivo
        String nombreArchivo = null;
        Part filePart = request.getPart("docSustentoFinal");
        if (filePart != null && filePart.getSize() > 0) {
            nombreArchivo = guardarArchivo(filePart);
        }

        token.setCodempcon2(usuario.getCempCoEmp());
        token.setUniconfirma2(dependenciaDAO.obtenerCodigoDependenciaUsuario(usuario.getCempCoEmp()));
        token.setNumdnitokcon2(dniConfirma);
        token.setCodemptokcon2(empConfirma.getCempCoEmp());
        token.setUniemptokcon2(empConfirma.getCempCoDepend());
        token.setFlgtokcon2(tieneToken);
        token.setEsttokcon2(estadoToken);

        if (estadoToken == 4 && unidadEntregaStr != null && !unidadEntregaStr.isEmpty()) {
            token.setUnienttokcon2(Integer.parseInt(unidadEntregaStr));
        }

        token.setFecentcon2(java.sql.Date.valueOf(fechaEntrega));
        token.setTxtobscon2(observaciones);
        token.setDocSustentoFinal(nombreArchivo);
        token.setUsumod(usuario.getIdUsuario());

        boolean ok = tokenDAO.actualizarConfirmacionFinal(token);

        if (ok) {
            request.setAttribute("success", "✓ Confirmación final registrada - Token COMPLETO");
            System.out.println("✓ Confirmación 2 del token " + idToken + " por: " + usuario.getUsername());
        } else {
            request.setAttribute("error", "Error al registrar confirmación final");
        }

        response.sendRedirect("tokens");

    }


    private void buscarEmpleadoPorDNI(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json; charset=UTF-8");

        String dni = request.getParameter("dni");

        if (!ValidationUtil.isValidDNI(dni)) {
            response.getWriter().write("{\"error\":\"DNI inválido\"}");
            return;
        }

        Empleado empleado = empleadoDAO.buscarPorDNI(dni);

        if (empleado == null) {
            response.getWriter().write("{\"error\":\"No se encontró empleado\"}");
            return;
        }

        String json = String.format(
                "{\"nombre\":\"%s\",\"estado\":\"%s\",\"dependencia\":\"%s\",\"codigo\":%d}",
                empleado.getNombreCompleto(),
                empleado.getEstadoTexto(),
                empleado.getDependencia(),
                empleado.getCempCoEmp()
        );

        response.getWriter().write(json);
    }

    private void obtenerTokenJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json; charset=UTF-8");

        try {
            int idToken = Integer.parseInt(request.getParameter("id"));
            Token token = tokenDAO.obtenerPorId(idToken);

            if (token == null) {
                response.getWriter().write("{\"error\":\"Token no encontrado\"}");
                return;
            }

            StringBuilder json = new StringBuilder("{");

            // ========== DATOS BÁSICOS ==========
            json.append("\"id\":").append(token.getIdasignatoken()).append(",");
            json.append("\"dni\":\"").append(escapeJson(token.getNumdnitok())).append("\",");
            json.append("\"nombre\":\"").append(escapeJson(token.getNombreUsuarioAsignado())).append("\",");
            json.append("\"dependencia\":\"").append(escapeJson(token.getNombreDependencia())).append("\",");
            json.append("\"accion\":\"").append(escapeJson(token.getTipAccionTexto())).append("\",");

            // Fecha de acción
            if (token.getFecaccion() != null) {
                json.append("\"fechaAccion\":\"").append(token.getFecaccion().toString()).append("\",");
            }

            // Estado del usuario
            json.append("\"estado\":\"").append(token.getEstTokenTexto()).append("\",");

            // Documento sustento inicial
            if (token.getDocSustento() != null && !token.getDocSustento().isEmpty()) {
                json.append("\"docSustento\":\"").append(escapeJson(token.getDocSustento())).append("\",");
            }

            // Código de unidad
            json.append("\"codigoUnidad\":").append(token.getUniregistra());

            // ========== PRIMERA CONFIRMACIÓN ==========
            if (token.getCodempcon() != null && token.getCodempcon() > 0) {
                json.append(",\"codempcon\":").append(token.getCodempcon());
                json.append(",\"dniConf1\":\"").append(escapeJson(token.getNumdnitokcon())).append("\"");
                json.append(",\"tieneTokenConf1\":\"").append(escapeJson(token.getFlgTokConTexto())).append("\"");

                // ⭐ CRÍTICO: Incluir el documento de confirmación 1
                if (token.getDocSustentoEntrega() != null && !token.getDocSustentoEntrega().isEmpty()) {
                    json.append(",\"docSustentoEntrega\":\"").append(escapeJson(token.getDocSustentoEntrega())).append("\"");
                } else {
                    json.append(",\"docSustentoEntrega\":\"\"");
                }

                if (token.getEsttokcon() != null) {
                    json.append(",\"estadoTokenConf1\":\"").append(escapeJson(getEstadoTexto(token.getEsttokcon()))).append("\"");
                }

                if (token.getUnienttokcon() != null) {
                    json.append(",\"unidadEntregaConf1\":").append(token.getUnienttokcon());
                }

                // Fecha de confirmación 1
                if (token.getFecentcon() != null) {
                    String fechaConf1 = token.getFecentcon().toString();
                    json.append(",\"fechaConf1\":\"").append(fechaConf1).append("\"");
                    json.append(",\"fechaEntregaConf1\":\"").append(fechaConf1).append("\"");
                }

                // Observaciones de confirmación 1
                if (token.getTxtobscon() != null && !token.getTxtobscon().isEmpty()) {
                    json.append(",\"obsConf1\":\"").append(escapeJson(token.getTxtobscon())).append("\"");
                }
                
            }

            // ========== SEGUNDA CONFIRMACIÓN ==========
            if (token.getCodempcon2() != null && token.getCodempcon2() > 0) {
                json.append(",\"codempcon2\":").append(token.getCodempcon2());
                json.append(",\"dniConf2\":\"").append(escapeJson(token.getNumdnitokcon2())).append("\"");

                if (token.getFlgtokcon2() != null) {
                    String tieneToken2 = token.getFlgtokcon2() == 1 ? "SIN TOKEN" : "CON TOKEN";
                    json.append(",\"tieneTokenConf2\":\"").append(tieneToken2).append("\"");
                }

                if (token.getEsttokcon2() != null) {
                    json.append(",\"estadoTokenConf2\":\"").append(escapeJson(getEstadoTexto(token.getEsttokcon2()))).append("\"");
                }

                // Documento sustento final
                if (token.getDocSustentoFinal() != null && !token.getDocSustentoFinal().isEmpty()) {
                    json.append(",\"docSustentoFinal\":\"").append(escapeJson(token.getDocSustentoFinal())).append("\"");
                }

                // Fecha de confirmación 2
                if (token.getFecentcon2() != null) {
                    json.append(",\"fechaConf2\":\"").append(token.getFecentcon2().toString()).append("\"");
                }

                // Observaciones de confirmación 2
                if (token.getTxtobscon2() != null && !token.getTxtobscon2().isEmpty()) {
                    json.append(",\"obsConf2\":\"").append(escapeJson(token.getTxtobscon2())).append("\"");
                }
            }

            // ========== INFORMACIÓN ADICIONAL ==========
            // DNI que recibe (si existe)
            if (token.getDniemprec() != null && !token.getDniemprec().isEmpty()) {
                json.append(",\"dniRecibe\":\"").append(escapeJson(token.getDniemprec())).append("\"");
            }

            // Tipo de acción
            json.append(",\"tipoAccion\":").append(token.getTipaccion());

            json.append("}");

            response.getWriter().write(json.toString());

            System.out.println("✓ Token JSON generado para ID: " + idToken);

        } catch (NumberFormatException e) {
            response.getWriter().write("{\"error\":\"ID de token inválido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\":\"Error al obtener token: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }


    private void restaurarToken(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        if (!usuario.isAdmin()) {
            request.setAttribute("error", "Solo ADMIN puede restaurar");
            listarTokens(request, response, usuario);
            return;
        }

        int idToken = Integer.parseInt(request.getParameter("id"));
        boolean ok = tokenDAO.restaurar(idToken);

        if (ok) {
            Object ultimoTokenOcultadoId = request.getSession().getAttribute("ultimoTokenOcultadoId");
            if (ultimoTokenOcultadoId != null && String.valueOf(ultimoTokenOcultadoId).equals(String.valueOf(idToken))) {
                request.getSession().removeAttribute("ultimoTokenOcultadoId");
            }
            request.setAttribute("success", "✓ Token restaurado correctamente");
            System.out.println("✓ Token " + idToken + " restaurado por: " + usuario.getUsername());
        } else {
            request.setAttribute("error", "Error al restaurar token");
        }

        listarTokens(request, response, usuario);
    }

    private void eliminarToken(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        if (!usuario.isAdmin()) {
            request.setAttribute("error", "Solo ADMIN puede eliminar");
            listarTokens(request, response, usuario);
            return;
        }

        int idToken = Integer.parseInt(request.getParameter("id"));
        boolean ok = tokenDAO.eliminar(idToken);

        if (ok) {
            request.getSession().setAttribute("ultimoTokenOcultadoId", idToken);
            request.setAttribute("success", "✓ Token ocultado correctamente (el registro se conserva)");
            System.out.println("✓ Token " + idToken + " eliminado por: " + usuario.getUsername());
        } else {
            request.setAttribute("error", "Error al eliminar");
        }

        listarTokens(request, response, usuario);
    }

    private void buscarTokens(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {

        String dni = request.getParameter("dni");
        String fechaDesde = request.getParameter("fechaDesde");
        String fechaHasta = request.getParameter("fechaHasta");
       
        List<Token> tokens = tokenDAO.buscar(dni, fechaDesde, fechaHasta, usuario.isAdmin(), usuario.getCempCoEmp());
        List<Dependencia> dependencias = dependenciaDAO.listarActivas();

        request.setAttribute("tokens", tokens);
        request.setAttribute("dependencias", dependencias);
        request.setAttribute("totalTokens", tokens.size());
        request.setAttribute("busquedaActiva", true);

        request.getRequestDispatcher("tokens.jsp").forward(request, response);
    }

    private void descargarArchivo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String fileName = request.getParameter("file");

        if (fileName == null || fileName.isEmpty()) {
            response.sendError(404, "Archivo no especificado");
            return;
        }

        File file = new File(UPLOAD_DIR + File.separator + fileName);

        if (!file.exists()) {
            response.sendError(404, "Archivo no encontrado");
            return;
        }

        String mimeType = FileUtil.getContentType(fileName);
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("✓ Archivo servido correctamente: " + fileName);
    }

    private String guardarArchivo(Part filePart) throws IOException {

        String fileName = getFileName(filePart);

        if (!FileUtil.isValidExtension(fileName)) {
            throw new IOException("Solo se permiten archivos PDF, JPG, PNG");
        }

        if (!FileUtil.isValidFileSize(filePart.getSize())) {
            throw new IOException("El archivo no debe superar los 5MB");
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String uniqueFileName = FileUtil.generateUniqueFileName(fileName);
        String filePath = UPLOAD_DIR + File.separator + uniqueFileName;

        filePart.write(filePath);

        System.out.println("✓ Archivo guardado en: " + filePath);

        return uniqueFileName;
    }

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

    private String getEstadoTexto(int estado) {
        switch (estado) {
            case 1:
                return "OPERATIVO";
            case 2:
                return "MALOGRADO";
            case 3:
                return "PERDIDO";
            case 4:
                return "ENTREGADO A UNIDAD ANTERIOR";
            default:
                return "N/A";
        }
    }
}
