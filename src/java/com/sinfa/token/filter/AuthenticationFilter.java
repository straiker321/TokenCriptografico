package com.sinfa.token.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;


@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {
    "/dashboard.jsp",
    "/tokens.jsp",
    "/reportes.jsp",
    "/usuarios.jsp",
    "/consultas.jsp",
    "/configuracion.jsp",
    "/auditoria.jsp",
    "/perfil.jsp",
    "/cambiar-password.jsp"
})
public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("✓ Filtro de autenticación inicializado");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String loginURI = httpRequest.getContextPath() + "/login.jsp";
        String requestURI = httpRequest.getRequestURI();
        
        // Verificar si hay sesión activa
        boolean loggedIn = (session != null && session.getAttribute("usuario") != null);
        
        if (loggedIn) {
            // Usuario autenticado, permitir acceso
            chain.doFilter(request, response);
        } else {
            // No autenticado, redirigir a login
            System.out.println("✗ Acceso denegado a: " + requestURI);
            httpResponse.sendRedirect(loginURI + "?timeout=true");
        }
    }
    
    @Override
    public void destroy() {
        System.out.println("✓ Filtro de autenticación destruido");
    }
}