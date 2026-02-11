package com.sinfa.token.servlet;

import com.sinfa.token.dao.UsuarioDAO;
import com.sinfa.token.model.Usuario;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {
    
    private UsuarioDAO usuarioDAO;
    
    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO();
    }
    
    /**
     * Maneja GET - Muestra formulario de login o cierra sesión
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        if ("/logout".equals(path)) {
            // Cerrar sesión
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuario");
                if (usuario != null) {
                    System.out.println("✓ Logout: " + usuario.getUsername());
                }
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login.jsp?logout=true");
        } else {
            // Verificar si ya hay sesión activa
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("usuario") != null) {
                response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            } else {
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        }
    }
    
    /**
     * Maneja POST - Procesa autenticación
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String recordar = request.getParameter("recordar");
        
        // Validar datos
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("error", "Usuario y contraseña son obligatorios");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        // Autenticar
        Usuario usuario = usuarioDAO.autenticar(username.trim(), password);
        
        if (usuario != null) {
            // Login exitoso
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            session.setAttribute("idUsuario", usuario.getIdUsuario());
            session.setAttribute("username", usuario.getUsername());
            session.setAttribute("nombreCompleto", usuario.getNombreCompleto());
            session.setAttribute("perfil", usuario.getNombrePerfil());
            session.setAttribute("dependencia", usuario.getDependencia());
            session.setAttribute("isAdmin", usuario.isAdmin());
            
            // Configurar tiempo de sesión (30 minutos)
            session.setMaxInactiveInterval(30 * 60);
            
            String perfil = usuario.getNombrePerfil();
            if(!"ADMINISTRADOR".equalsIgnoreCase(perfil)
                    && !"IMPLEMENTADOR".equalsIgnoreCase(perfil)){
            session.invalidate();
            request.setAttribute("error", "perfil no autorizado");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
            }
            
            // Recordar usuario si se seleccionó
            if ("on".equals(recordar)) {
                Cookie userCookie = new Cookie("rememberedUser", username);
                userCookie.setMaxAge(7 * 24 * 60 * 60); // 7 días
                response.addCookie(userCookie);
            }
            
            System.out.println("✓ Login exitoso: " + username + " (" + usuario.getNombrePerfil() + ")");
            
            // Redirigir al dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            
        } else {
            // Login fallido
            System.out.println("✗ Login fallido: " + username);
            request.setAttribute("error", "Usuario o contraseña incorrectos");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}