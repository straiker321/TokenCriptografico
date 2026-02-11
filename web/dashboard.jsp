<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.sinfa.token.model.Usuario"%>
<%@page import="com.sinfa.token.dao.TokenDAO"%>
<%
    // Verificar sesión
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String nombreCompleto = usuario.getNombreCompleto();
    String perfil = usuario.getNombrePerfil();
    String dependencia = usuario.getDependencia();
    boolean isAdmin = usuario.isAdmin();
    boolean isImplementador = "IMPLEMENTADOR" .equalsIgnoreCase(usuario.getNombrePerfil());
    
    // Obtener estadísticas reales
    TokenDAO tokenDAO = new TokenDAO();
    int totalTokens = 0;
    int tokensOperativos = 0;
    int tokensPendientes = 0;
    int tokensProblemas = 0;
    
    try {
        totalTokens = tokenDAO.contarTotales();
        tokensOperativos = tokenDAO.contarOperativos();
        tokensPendientes = tokenDAO.contarPendientes();
        tokensProblemas = tokenDAO.contarConProblemas();
    } catch (Exception e) {
        System.err.println("Error al cargar estadísticas: " + e.getMessage());
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Sistema de Tokens</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <!-- SIDEBAR -->
    <aside class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <div class="logo">
                <i class="fas fa-shield-alt"></i>
                <span>SINFA</span>
            </div>
            <button class="toggle-btn" onclick="toggleSidebar()">
                <i class="fas fa-bars"></i>
            </button>
        </div>
        
        <nav class="sidebar-nav">
            <ul>
                <li class="nav-item active">
                    <a href="dashboard.jsp">
                        <i class="fas fa-home"></i>
                        <span>Inicio</span>
                    </a>
                </li>
                
                <li class="nav-item">
                    <a href="tokens">
                        <i class="fas fa-key"></i>
                        <span>Gestión de Tokens</span>
                    </a>
                </li>
                
                
                
                <% if (isAdmin) { %>
                <li class="nav-divider"></li>
                <li class="nav-title">ADMINISTRACIÓN</li>
                
                <li class="nav-item">
                    <a href="usuarios.jsp">
                        <i class="fas fa-users"></i>
                        <span>Usuarios</span>
                    </a>
                </li>
                
                <% } %>
            </ul>
        </nav>
        
        <div class="sidebar-footer">
            <div class="user-info">
                <div class="user-avatar">
                    <i class="fas fa-user"></i>
                </div>
                <div class="user-details">
                    <p class="user-name"><%= nombreCompleto %></p>
                    <p class="user-role"><%= perfil %></p>
                </div>
            </div>
        </div>
    </aside>
    
    <!-- MAIN CONTENT -->
    <div class="main-content" id="mainContent">
        <!-- HEADER -->
        <header class="top-header">
            <div class="header-left">
                <button class="mobile-toggle" onclick="toggleSidebar()">
                    <i class="fas fa-bars"></i>
                </button>
                <h1>Sistema de Gestión de Tokens Criptográficos</h1>
            </div>
            
            <div class="header-right">
                <div class="header-item">
                    <i class="fas fa-building"></i>
                    <span><%= dependencia %></span>
                </div>
                
                <div class="header-item">
                    <i class="fas fa-clock"></i>
                    <span id="currentTime"></span>
                </div>
                
                <div class="user-menu">
                    <button class="user-menu-btn" onclick="toggleUserMenu()">
                        <div class="user-avatar-small">
                            <i class="fas fa-user"></i>
                        </div>
                        <div class="user-info-header">
                            <span class="user-name"><%= nombreCompleto %></span>
                            <span class="user-role-badge <%= isAdmin ? "badge-admin" : (isImplementador ? "badge-impl-admin" : "badge-impl") %>">
                                <%= perfil %>
                            </span>
                        </div>
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    
                    <div class="user-dropdown" id="userDropdown">
                        <div class="dropdown-header">
                            <p class="dropdown-name"><%= nombreCompleto %></p>
                            <p class="dropdown-email"><%= usuario.getUsername() %></p>
                        </div>
                        <div class="dropdown-divider"></div>
                        <a href="perfil.jsp" class="dropdown-item">
                            <i class="fas fa-user-circle"></i>
                            Mi Perfil
                        </a>
                        <div class="dropdown-divider"></div>
                        <a href="logout" class="dropdown-item logout-item">
                            <i class="fas fa-sign-out-alt"></i>
                            Cerrar Sesión
                        </a>
                    </div>
                </div>
            </div>
        </header>
        
        <!-- DASHBOARD CONTENT -->
        <div class="dashboard-container">
            <!-- WELCOME BANNER -->
            <div class="welcome-banner">
                <div class="welcome-content">
                    <h2>¡Bienvenido, <%= nombreCompleto.split(" ")[0] %>!</h2>
                    <p>Gestiona los tokens criptográficos de manera eficiente y segura</p>
                </div>
                <div class="welcome-icon">
                    <i class="fas fa-hand-sparkles"></i>
                </div>
            </div>
            
            <!-- STATISTICS CARDS -->
            <div class="stats-grid">
                <div class="stat-card stat-primary">
                    <div class="stat-icon">
                        <i class="fas fa-key"></i>
                    </div>
                    <div class="stat-content">
                        <h3><%= totalTokens %></h3>
                        <p>Tokens Totales</p>
                    </div>
                </div>
                
                <div class="stat-card stat-success">
                    <div class="stat-icon">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="stat-content">
                        <h3><%= tokensOperativos %></h3>
                        <p>Tokens Operativos</p>
                    </div>
                </div>
                
                <div class="stat-card stat-warning">
                    <div class="stat-icon">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-content">
                        <h3><%= tokensPendientes %></h3>
                        <p>Pendientes de Confirmación</p>
                    </div>
                </div>
                
                <div class="stat-card stat-danger">
                    <div class="stat-icon">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <div class="stat-content">
                        <h3><%= tokensProblemas %></h3>
                        <p>Con Problemas</p>
                    </div>
                </div>
            </div>
            
            <!-- QUICK ACTIONS -->
            <div class="quick-actions">
                <h3><i class="fas fa-bolt"></i> Acciones Rápidas</h3>
                <div class="actions-grid">
                    <a href="tokens" class="action-card">
                        <i class="fas fa-plus-circle"></i>
                        <span>Asignar Nuevo Token</span>
                    </a>
                    <a href="tokens?action=list" class="action-card">
                        <i class="fas fa-search"></i>
                        <span>Buscar Token</span>
                    </a>
                    <% if (isAdmin || isImplementador) { %>
                    <a href="tokens?action=exportExcel" class="action-card">
                        <i class="fas fa-file-excel"></i>
                        <span>Exportar a Excel</span>
                    </a>
                    <% } %>
                    <a href="tokens?action=list" class="action-card">
                        <i class="fas fa-tasks"></i>
                        <span>Ver Pendientes</span>
                    </a>
                </div>
            </div>
            
            <!-- RECENT ACTIVITY -->
            <div class="recent-activity">
                <h3><i class="fas fa-history"></i> Información del Sistema</h3>
                <div class="activity-list">
                    <div class="activity-item">
                        <div class="activity-icon activity-success">
                            <i class="fas fa-check"></i>
                        </div>
                        <div class="activity-content">
                            <p class="activity-title">Sistema Operativo</p>
                            <p class="activity-time">Todos los servicios funcionando correctamente</p>
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon activity-info">
                            <i class="fas fa-database"></i>
                        </div>
                        <div class="activity-content">
                            <p class="activity-title">Base de Datos Conectada</p>
                            <p class="activity-time">PostgreSQL - token_db</p>
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-icon activity-warning">
                            <i class="fas fa-user-shield"></i>
                        </div>
                        <div class="activity-content">
                            <p class="activity-title">Perfil: <%= perfil %></p>
                            <p class="activity-time"><%= isAdmin ? "Acceso total al sistema" : "Acceso según permisos asignados" %></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- FOOTER -->
        <footer class="dashboard-footer">
            <p>© 2025 SINFA - Sistema de Gestión de Tokens Criptográficos v1.0</p>
        </footer>
    </div>
    
    <script src="${pageContext.request.contextPath}/assets/js/dashboard.js"></script>
    <script>
        // Actualizar reloj
        function updateClock() {
            const now = new Date();
            const timeString = now.toLocaleTimeString('es-PE', { 
                hour: '2-digit', 
                minute: '2-digit',
                second: '2-digit'
            });
            const element = document.getElementById('currentTime');
            if (element) {
                element.textContent = timeString;
            }
        }
        
        setInterval(updateClock, 1000);
        updateClock();
        
        // Toggle sidebar
        function toggleSidebar() {
            const sidebar = document.getElementById('sidebar');
            const mainContent = document.getElementById('mainContent');
            if (sidebar && mainContent) {
                sidebar.classList.toggle('collapsed');
                mainContent.classList.toggle('expanded');
            }
        }
        
        // Toggle user menu
        function toggleUserMenu() {
            const dropdown = document.getElementById('userDropdown');
            if (dropdown) {
                dropdown.classList.toggle('show');
            }
        }
        
        // Cerrar dropdown al hacer clic fuera
        window.onclick = function(event) {
            if (!event.target.matches('.user-menu-btn') && 
                !event.target.closest('.user-menu-btn')) {
                const dropdown = document.getElementById('userDropdown');
                if (dropdown && dropdown.classList.contains('show')) {
                    dropdown.classList.remove('show');
                }
            }
        }
    </script>
</body>
</html>