<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema de Tokens Criptográficos</title>
    <link rel="stylesheet" href="assets/css/login.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="login-container">
        <!-- Lado izquierdo - Información -->
        <div class="login-left">
            <div class="login-logo">
                <i class="fas fa-shield-alt"></i>
                <h1>SINFA</h1>
            </div>
            <h2>Sistema de Gestión de<br>Tokens Criptográficos</h2>
            <p class="login-description">
                Control y seguimiento de tokens criptográficos entregados por SINFA.
                Sistema seguro y confiable para la gestión de dispositivos de autenticación.
            </p>
            <div class="login-features">
                <div class="feature">
                    <i class="fas fa-check-circle"></i>
                    <span>Control total de asignaciones</span>
                </div>
                <div class="feature">
                    <i class="fas fa-check-circle"></i>
                    <span>Trazabilidad completa</span>
                </div>
                <div class="feature">
                    <i class="fas fa-check-circle"></i>
                    <span>Reportes en tiempo real</span>
                </div>
            </div>
        </div>
        
        <!-- Lado derecho - Formulario -->
        <div class="login-right">
            <div class="login-form-container">
                <div class="login-header">
                    <h3>Iniciar Sesión</h3>
                    <p>Ingrese sus credenciales para acceder al sistema</p>
                </div>
                
                <!-- Mensajes de alerta -->
                <% 
                String error = (String) request.getAttribute("error");
                String logout = request.getParameter("logout");
                String timeout = request.getParameter("timeout");
                String savedUser = request.getAttribute("username") != null ? 
                                   (String) request.getAttribute("username") : "";
                
                // Obtener usuario recordado de cookie
                Cookie[] cookies = request.getCookies();
                if (cookies != null && savedUser.isEmpty()) {
                    for (Cookie cookie : cookies) {
                        if ("rememberedUser".equals(cookie.getName())) {
                            savedUser = cookie.getValue();
                            break;
                        }
                    }
                }
                %>
                
                <% if (error != null) { %>
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    <%= error %>
                </div>
                <% } %>
                
                <% if (logout != null) { %>
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    Sesión cerrada correctamente
                </div>
                <% } %>
                
                <% if (timeout != null) { %>
                <div class="alert alert-warning">
                    <i class="fas fa-clock"></i>
                    Su sesión ha expirado. Por favor, inicie sesión nuevamente
                </div>
                <% } %>
                
                <!-- Formulario de login -->
                <form action="login" method="POST" id="loginForm">
                    <div class="form-group">
                        <label for="username">
                            <i class="fas fa-user"></i>
                            Usuario
                        </label>
                        <input type="text" 
                               id="username" 
                               name="username" 
                               class="form-control"
                               placeholder="Ingrese su usuario"
                               value="<%= savedUser %>"
                               required
                               autofocus>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">
                            <i class="fas fa-lock"></i>
                            Contraseña
                        </label>
                        <div class="password-input">
                            <input type="password" 
                                   id="password" 
                                   name="password" 
                                   class="form-control"
                                   placeholder="Ingrese su contraseña"
                                   required>
                            <button type="button" class="toggle-password" onclick="togglePassword()">
                                <i class="fas fa-eye" id="toggleIcon"></i>
                            </button>
                        </div>
                    </div>
                    
                    <div class="form-options">
                        <label class="checkbox-container">
                            <input type="checkbox" name="recordar">
                            <span class="checkmark"></span>
                            Recordar usuario
                        </label>
                    </div>
                    
                    <button type="submit" class="btn-login">
                        <i class="fas fa-sign-in-alt"></i>
                        Iniciar Sesión
                    </button>
                </form>
                
                <div class="login-footer">
                    <p><i class="fas fa-info-circle"></i> 
                    ¿Problemas para acceder? Contacte al administrador del sistema</p>
                </div>
            </div>
            
            <div class="version-info">
                <p>Sistema de Tokens Criptográficos v1.0</p>
                <p>© 2025 SINFA - Inspectoría General</p>
            </div>
        </div>
    </div>
    
    <script>
        // Toggle password visibility
        function togglePassword() {
            const passwordInput = document.getElementById('password');
            const toggleIcon = document.getElementById('toggleIcon');
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                toggleIcon.classList.remove('fa-eye');
                toggleIcon.classList.add('fa-eye-slash');
            } else {
                passwordInput.type = 'password';
                toggleIcon.classList.remove('fa-eye-slash');
                toggleIcon.classList.add('fa-eye');
            }
        }
        
        // Validación del formulario
        document.getElementById('loginForm').addEventListener('submit', function(e) {
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value;
            
            if (username === '' || password === '') {
                e.preventDefault();
                alert('Por favor complete todos los campos');
                return false;
            }
        });
        
        // Auto-ocultar alertas después de 5 segundos
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                alert.style.opacity = '0';
                setTimeout(function() {
                    alert.style.display = 'none';
                }, 500);
            });
        }, 5000);
    </script>
</body>
</html>