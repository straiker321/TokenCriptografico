<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.sinfa.token.model.*"%>
<%@page import="com.sinfa.token.dao.DependenciaDAO"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    boolean isAdmin = usuario.isAdmin();
    
    @SuppressWarnings("unchecked")
    List<Token> tokens = (List<Token>) request.getAttribute("tokens");
    @SuppressWarnings("unchecked")
    List<Dependencia> dependencias = (List<Dependencia>) request.getAttribute("dependencias");
    
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    String warning = (String) request.getAttribute("warning");
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    DependenciaDAO depDAO = new DependenciaDAO();
    int unidadUsuario = depDAO.obtenerCodigoDependenciaUsuario(usuario.getCempCoEmp());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Tokens - SINFA</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
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
                <li class="nav-item">
                    <a href="dashboard.jsp"><i class="fas fa-home"></i><span>Inicio</span></a>
                </li>
                <li class="nav-item active">
                    <a href="tokens"><i class="fas fa-key"></i><span>Gestión de Tokens</span></a>
                </li>
                <% if (isAdmin) { %>
                <li class="nav-divider"></li>
                <li class="nav-title">ADMINISTRACIÓN</li>
                <li class="nav-item">
                    <a href="usuarios.jsp"><i class="fas fa-users"></i><span>Usuarios</span></a>
                </li>
                <% } %>
            </ul>
        </nav>
    </aside>

    <!-- MAIN CONTENT -->
    <div class="main-content" id="mainContent">
        <!-- HEADER -->
        <header class="top-header">
            <div class="header-left">
                <button class="mobile-toggle" onclick="toggleSidebar()"><i class="fas fa-bars"></i></button>
                <h1>Gestión de Tokens Criptográficos</h1>
            </div>
            
            <div class="header-right">
                <div class="header-item">
                    <i class="fas fa-building"></i>
                    <span><%= usuario.getDependencia() %></span>
                </div>
                
                <div class="user-menu">
                    <button class="user-menu-btn" onclick="toggleUserMenu()">
                        <div class="user-avatar-small"><i class="fas fa-user"></i></div>
                        <div class="user-info-header">
                            <span class="user-name"><%= usuario.getNombreCompleto() %></span>
                            <span class="user-role-badge <%= isAdmin ? "badge-admin" : "badge-impl" %>">
                                <%= usuario.getNombrePerfil() %>
                            </span>
                        </div>
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    
                    <div class="user-dropdown" id="userDropdown">
                        <a href="logout" class="dropdown-item logout-item">
                            <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
                        </a>
                    </div>
                </div>
            </div>
        </header>

        <!-- CONTENIDO -->
        <div class="tokens-container">
            
            <!-- MENSAJES -->
            <% if (error != null) { %>
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i> <%= error %>
                <button class="alert-close" onclick="this.parentElement.remove()">&times;</button>
            </div>
            <% } %>
            
            <% if (success != null) { %>
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i> <%= success %>
                <button class="alert-close" onclick="this.parentElement.remove()">&times;</button>
            </div>
            <% } %>
            
            <% if (warning != null) { %>
            <div class="alert alert-warning">
                <i class="fas fa-exclamation-triangle"></i> <%= warning %>
                <button class="alert-close" onclick="this.parentElement.remove()">&times;</button>
            </div>
            <% } %>
            
            <!-- BARRA DE BÚSQUEDA -->
            <div class="search-bar">
               <form action="tokens" method="GET" class="search-form" onsubmit="return validarBusqueda()">
                   <input type="hidden" name="action" value="buscar">
                   <div class="search-inputs">
                       <input type="text" 
                              id="dniBusqueda"
                              name="dni" 
                              placeholder="Buscar por DNI (8 dígitos)" 
                              class="form-control"
                              pattern="[0-9]{8}"
                              maxlength="8">

                       <input type="date" 
                              name="fechaDesde" 
                              id="fechaDesde"
                              class="form-control"
                              value="${fecIni}"
                              placeholder="Fecha desde">

                       <input type="date" 
                              name="fechaHasta" 
                              id="fechaHasta"
                              class="form-control"
                              value="${fecFin}"
                              placeholder="Fecha hasta">

                       <button type="submit" class="btn btn-secondary">
                           <i class="fas fa-search"></i> Buscar
                       </button>

                       <a href="tokens" class="btn btn-secondary">
                           <i class="fas fa-redo"></i> Limpiar
                       </a>

                       <button type="button" class="btn btn-success" onclick="exportarPDF()">
                           <i class="fas fa-file-pdf"></i> Exportar PDF
                       </button>
                   </div>
               </form>
 
                <% if (isAdmin) { %>
                <button class="btn btn-primary" onclick="mostrarModal('modalAsignar')">
                    <i class="fas fa-plus"></i> Asignar Token
                </button>
                <% } %>
            </div>
            
            <!-- TABLA DE TOKENS -->
            <div class="tokens-table-container">
                <table class="tokens-table">
                    <thead>
                        <tr>
                            <th>N°</th>
                            <th>Fecha</th>
                            <th>DNI</th>
                            <th>Usuario</th>
                            <th>Dependencia</th>
                            <th>Estado</th>
                            <th>Acción</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (tokens != null && !tokens.isEmpty()) {
                            int i = 1;
                            for (Token token : tokens) { 
                                // PERMISOS SEGÚN DOCUMENTO DE ANÁLISIS:
                                // 1.a.i: ADMIN ve ícono editar SIEMPRE
                                // 1.a.ii: NO ADMIN ve ícono editar si CODEMPCON = NULL o CODEMPCON2 = NULL
                                
                                boolean mostrarEditar = false;
                                
                                if (isAdmin) {
                                    
                                    mostrarEditar = true;
                                } else {
                                    // NO ADMIN solo ve ícono si hay confirmaciones pendientes
                                    if (token.getCodempcon() == null || token.getCodempcon2() == null) {
                                        mostrarEditar = true;
                                    }
                                }
                                
                                // Solo ADMIN puede eliminar
                                boolean mostrarEliminar = isAdmin;
                        %>
                        <tr>
                            <td><%= i++ %></td>
                            <td><%= sdf.format(token.getFecaccion()) %></td>
                            <td><%= token.getNumdnitok() %></td>
                            <td><%= token.getNombreUsuarioAsignado() %></td>
                            <td><%= token.getNombreDependencia()%></td>
                            <td>
                                <% if (token.isCompleto()) { %>
                                <span class="badge badge-success">✓ COMPLETO</span>
                                <% } else if (token.isPendienteConfirmacionFinal()) { %>
                                <span class="badge badge-info">⏳ PEND. CONF. FINAL</span>
                                <% } else if (token.isPendienteConfirmacionInicial()) { %>
                                <span class="badge badge-warning">⏳ PEND. CONF. INICIAL</span>
                                <% } %>
                            </td>
                            <td><%= token.getTipAccionTexto() %></td>
                            <td>
                                <div class="action-buttons">
                                    <!-- VER DETALLES: TODOS -->
                                    <button class="btn-icon btn-view" 
                                            onclick="verDetalle(<%= token.getIdasignatoken() %>)"
                                            title="Ver Detalles">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    
                                    <!-- EDITAR/CONFIRMAR: ADMIN siempre, NO ADMIN si hay pendientes -->
                                    <% if (mostrarEditar) { %>
                                    <button class="btn-icon btn-edit" 
                                            onclick="editarToken(<%= token.getIdasignatoken() %>)"
                                            title="<%= isAdmin ? "Editar" : "Confirmar" %>">
                                        <i class="fas fa-<%= isAdmin ? "edit" : "check" %>"></i>
                                    </button>
                                    <% } %>
                                    
                                    <!-- ELIMINAR: Solo ADMIN -->
                                    <% if (mostrarEliminar) { %>
                                    <button class="btn-icon btn-delete" 
                                            onclick="eliminarToken(<%= token.getIdasignatoken() %>)"
                                            title="Eliminar">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                        <% }} else { %>
                        <tr>
                            <td colspan="8" style="text-align: center; padding: 30px;">
                                <i class="fas fa-inbox" style="font-size: 48px; color: #ccc;"></i>
                                <p style="margin-top: 10px; color: #666;">No hay tokens registrados</p>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <!-- MODAL: EDITAR TOKEN (ADMIN) -->
    <div class="modal" id="modalEditarAdmin">
        <div class="modal-content modal-large">
            <div class="modal-header">
                <h3><i class="fas fa-edit"></i> EDITAR TOKEN - ADMINISTRADOR</h3>
                <button class="modal-close" onclick="cerrarModal('modalEditarAdmin')">&times;</button>
            </div>
            
            <form action="tokens" method="POST" enctype="multipart/form-data" id="formEditarAdmin">
                <input type="hidden" name="action" value="actualizar">
                <input type="hidden" name="idToken" id="idTokenEditar">
                
                <div class="form-section" style="background: #fef3c7; border-left-color: #f59e0b;">
                    <h4><i class="fas fa-database"></i> DATOS DEL REGISTRO</h4>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Registrado Por <span style="color: red;">(*)</span></label>
                            <select name="unidadRegistra" id="unidadRegistraEdit" class="form-control" required>
                                <% if (dependencias != null) for (Dependencia dep : dependencias) { %>
                                <option value="<%= dep.getCoDependencia() %>"><%= dep.getDeDependencia() %></option>
                                <% } %>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label>Acción <span style="color: red;">(*)</span></label>
                            <select name="accion" id="accionEdit" class="form-control" required>
                                <option value="1">EMISIÓN</option>
                                <option value="2">INTERNAMIENTO</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>DNI Usuario Asignado <span style="color: red;">(*)</span></label>
                            <input type="text" name="dniUsuarioAsigna" id="dniAsignaEdit" class="form-control" 
                                   pattern="[0-9]{8}" maxlength="8"
                                   onblur="buscarEmpleado(this.value, 'editAsigna')" required>
                        </div>
                        <div class="form-group">
                            <label>Nombre</label>
                            <input type="text" id="nombreEditAsigna" class="form-control" readonly>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Estado Usuario</label>
                            <input type="text" id="estadoEditAsigna" class="form-control" readonly>
                        </div>
                        <div class="form-group">
                            <label>Dependencia</label>
                            <input type="text" id="dependenciaEditAsigna" class="form-control" readonly>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>DNI Usuario que Recibe</label>
                            <input type="text" name="dniUsuarioRecibe" id="dniRecibeEdit" class="form-control" 
                                   pattern="[0-9]{8}" maxlength="8">
                        </div>
                        <div class="form-group">
                            <label>Fecha de Acción <span style="color: red;">(*)</span></label>
                            <input type="date" name="fechaAccion" id="fechaAccionEdit" class="form-control" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Documento Sustento Actual</label>
                        <div style="display: flex; gap: 10px; align-items: center;">
                            <input type="text" id="docSustentoActual" class="form-control" readonly style="flex: 1;">
                            <button type="button" class="btn btn-secondary" onclick="verArchivoModal('docSustentoActual')" style="padding: 8px 16px;">
                                <i class="fas fa-file-pdf"></i> Ver
                            </button>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Nuevo Documento Sustento (Dejar vacío si no desea cambiar)</label>
                        <input type="file" name="docSustento" class="form-control" accept=".pdf,.jpg,.jpeg,.png">
                        <small class="form-hint">PDF, JPG, PNG (Máx. 5MB)</small>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-danger" onclick="eliminarTokenDesdeModal()">
                        <i class="fas fa-trash"></i> ELIMINAR
                    </button>
                    <button type="button" class="btn btn-secondary" onclick="cerrarModal('modalEditarAdmin')">
                        <i class="fas fa-times"></i> CANCELAR
                    </button>
                    <button type="submit" class="btn btn-success">
                        <i class="fas fa-save"></i> ACTUALIZAR
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <!-- MODAL: ASIGNAR TOKEN -->
    <div class="modal" id="modalAsignar">
        <div class="modal-content">
            <div class="modal-header">
                <h3><i class="fas fa-plus-circle"></i> Asignar Nuevo Token</h3>
                <button class="modal-close" onclick="cerrarModal('modalAsignar')">&times;</button>
            </div>
            
            <form action="tokens" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="action" value="crear">
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Registrado Por <span style="color: red;">(*)</span></label>
                        <select name="unidadRegistra" class="form-control" <%= !isAdmin ? "disabled" : "" %> required>
                            <% if (dependencias != null) for (Dependencia dep : dependencias) { %>
                            <option value="<%= dep.getCoDependencia() %>" 
                                    <%= dep.getCoDependencia() == unidadUsuario ? "selected" : "" %>>
                                <%= dep.getDeDependencia() %>
                            </option>
                            <% } %>
                        </select>
                        <% if (!isAdmin) { %>
                        <input type="hidden" name="unidadRegistra" value="<%= unidadUsuario %>">
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label>Acción <span style="color: red;">(*)</span></label>
                        <select name="accion" class="form-control" <%= !isAdmin ? "disabled" : "" %> required>
                            <option value="1" selected>EMISIÓN</option>
                            <option value="2">INTERNAMIENTO</option>
                        </select>
                        <% if (!isAdmin) { %>
                        <input type="hidden" name="accion" value="1">
                        <% } %>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>DNI Usuario a Asignar <span style="color: red;">(*)</span></label>
                        <input type="text" name="dniUsuarioAsigna" class="form-control" 
                               pattern="[0-9]{8}" maxlength="8" placeholder="12345678"
                               onblur="buscarEmpleado(this.value, 'asigna')" required>
                    </div>
                    
                    <div class="form-group">
                        <label>Nombre</label>
                        <input type="text" id="nombreAsigna" class="form-control" readonly>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Estado</label>
                        <input type="text" id="estadoAsigna" class="form-control" readonly>
                    </div>
                    
                    <div class="form-group">
                        <label>Dependencia</label>
                        <input type="text" id="dependenciaAsigna" class="form-control" readonly>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>DNI Usuario que Recibe</label>
                        <input type="text" name="dniUsuarioRecibe" class="form-control" 
                               pattern="[0-9]{8}" maxlength="8">
                    </div>
                    
                    <div class="form-group">
                        <label>Fecha de Acción <span style="color: red;">(*)</span></label>
                        <input type="date" name="fechaAccion" class="form-control" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label>Documento Sustento (PDF, JPG, PNG)</label>
                    <input type="file" name="docSustento" class="form-control" accept=".pdf,.jpg,.jpeg,.png">
                    <small class="form-hint">Máximo 5MB</small>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="cerrarModal('modalAsignar')">
                        <i class="fas fa-times"></i> Cancelar
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Grabar
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <!-- MODAL: CONFIRMAR TOKEN -->
    <div class="modal" id="modalConfirmarToken">
        <div class="modal-content modal-large">
            <div class="modal-header">
                <h3><i class="fas fa-check-circle"></i> CONFIRMAR ASIGNACIÓN DE TOKEN</h3>
                <button class="modal-close" onclick="cerrarModal('modalConfirmarToken')">&times;</button>
            </div>
            
            <form action="tokens" method="POST" enctype="multipart/form-data" id="formConfirmarToken">
                <input type="hidden" name="action" id="actionConfirmar">
                <input type="hidden" name="idToken" id="idTokenConfirmar">
                
                <!-- SECCIÓN 1: DATOS DEL TOKEN ENTREGADO -->
                <div class="form-section" style="background: #e8f4f8; border-left-color: #2196f3;">
                    <h4><i class="fas fa-info-circle"></i> DATOS DE TOKEN ENTREGADO</h4>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>DNI del Usuario</label>
                            <input type="text" class="form-control" id="dniTokenReg" readonly>
                        </div>
                        <div class="form-group">
                            <label>Nombre</label>
                            <input type="text" class="form-control" id="nombreTokenReg" readonly>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Estado Usuario</label>
                            <input type="text" class="form-control" id="estadoTokenReg" readonly>
                        </div>
                        <div class="form-group">
                            <label>Dependencia del Usuario</label>
                            <input type="text" class="form-control" id="dependenciaTokenReg" readonly>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Acción</label>
                            <input type="text" class="form-control" id="accionTokenReg" readonly>
                        </div>
                        <div class="form-group">
                            <label>Fecha de acción</label>
                            <input type="date" class="form-control" id="fechaAccionReg" readonly>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Doc Sustento</label>
                            <div style="display: flex; gap: 10px; align-items: center;">
                                <input type="text" class="form-control" id="docSustentoReg" readonly style="flex: 1;">
                                <button type="button" class="btn btn-secondary" onclick="verArchivoModal('docSustentoReg')" style="padding: 8px 16px;">
                                    <i class="fas fa-file-pdf"></i> Ver
                                </button>
                            </div>
                        </div>
                        <div class="form-group"></div>
                    </div>
                </div>
                
                <!-- SECCIÓN 2: CONFIRMAR ASIGNACIÓN DEL TOKEN -->
                <div class="form-section" id="seccionConfirmacionInicial" style="background: #fff8e1; border-left-color: #ff9800;">
                    <h4><i class="fas fa-check"></i> CONFIRMAR ASIGNACIÓN INICIAL DEL TOKEN</h4>
                    
                    <div class="form-row">
                        <div class="form-group">       
                            <label>DNI del Usuario al que se asignó <span style="color: red;">(*)</span></label>
                            <input type="text" name="dniConfirma" class="form-control" 
                                   pattern="[0-9]{8}" maxlength="8" id="dniConf1"
                                   onblur="buscarEmpleado(this.value, 'conf1')" required>
                        </div>
                        <div class="form-group">
                            <label>Nombre</label>
                            <input type="text" class="form-control" id="nombreConf1" readonly>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Estado Usuario</label>
                            <input type="text" class="form-control" id="estadoConf1" readonly>
                        </div>
                        <div class="form-group">
                            <label>Dependencia del Usuario</label>
                            <input type="text" class="form-control" id="dependenciaConf1" readonly>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>¿Tiene TOKEN? <span style="color: red;">(*)</span></label>
                            <select name="tieneToken" class="form-control" required>
                                <option value="">-- Seleccione --</option>
                                <option value="1">SIN TOKEN</option>
                                <option value="2">CON TOKEN</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Estado del TOKEN <span style="color: red;">(*)</span></label>
                            <select name="estadoToken" class="form-control" 
                                    onchange="mostrarUnidad(this.value, '1')" required>
                                <option value="">-- Seleccione --</option>
                                <option value="1">OPERATIVO</option>
                                <option value="2">MALOGRADO</option>
                                <option value="3">PERDIDO</option>
                                <option value="4">ENTREGADO A UNIDAD ANTERIOR</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-row" id="rowUnidad1" style="display: none;">
                        <div class="form-group">
                            <label>Unidad a la que se entregó <span style="color: red;">(*)</span></label>
                            <select name="unidadEntrega" class="form-control">
                                <option value="">-- Seleccione --</option>
                                <% if (dependencias != null) for (Dependencia dep : dependencias) { %>
                                <option value="<%= dep.getCoDependencia() %>"><%= dep.getDeDependencia() %></option>
                                <% } %>
                            </select>
                        </div>
                        <div class="form-group"></div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Doc Sustento de entrega registrado</label>
                            <div style="display: flex; gap: 10px; align-items: center;">
                                <input type="text" class="form-control" id="docSustentoConf1" readonly style="flex: 1;" value="Sin archivo">
                                <button type="button" class="btn btn-secondary" onclick="verArchivoModal('docSustentoConf1')" style="padding: 8px 16px;">
                                    <i class="fas fa-file-pdf"></i> Ver
                                </button>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Fecha de entrega <span style="color: red;">(*)</span></label>
                            <input type="date" name="fechaEntrega" id="fechaEntregaConf1" class="form-control" required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Nuevo Doc Sustento de entrega</label>
                            <input type="file" name="docSustentoEntrega" class="form-control" 
                                   accept=".pdf,.jpg,.jpeg,.png">
                            <small class="form-hint">PDF, JPG, PNG (Máx. 5MB)</small>
                        </div>
                        <div class="form-group"></div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group" style="grid-column: 1 / -1;">
                            <label>Observaciones</label>
                            <textarea name="observaciones" id="observacionesConf1" class="form-control" rows="3"></textarea>
                        </div>
                    </div>
                </div>
                
                <!-- SECCIÓN 3: CONFIRMAR ASIGNACIÓN FINAL -->
                <div class="form-section" id="seccionConfirmacionFinal" style="display: none; background: #e8f5e9; border-left-color: #4caf50;">
                    <h4><i class="fas fa-check-double"></i> CONFIRMAR ASIGNACIÓN FINAL</h4>
                    
                    <div style="background: white; padding: 15px; border-radius: 6px; margin-bottom: 15px;">
                        <p style="margin: 0; font-weight: 600; color: #666; margin-bottom: 10px;">
                            <i class="fas fa-info-circle"></i> Primera Confirmación:
                        </p>
                        <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; font-size: 13px;">
                            <div><strong>DNI:</strong> <span id="datoDniConf1">-</span></div>
                            <div><strong>Tiene Token:</strong> <span id="datoTieneConf1">-</span></div>
                            <div><strong>Estado:</strong> <span id="datoEstadoConf1">-</span></div>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>DNI del Usuario al que se asignó <span style="color: red;">(*)</span></label>
                            <input type="text" name="dniConfirma2" class="form-control" 
                                   pattern="[0-9]{8}" maxlength="8" id="dniConf2"
                                   onblur="buscarEmpleado(this.value, 'conf2')" required>
                        </div>
                        <div class="form-group">
                            <label>Nombre</label>
                            <input type="text" class="form-control" id="nombreConf2" readonly>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Estado Usuario</label>
                            <input type="text" class="form-control" id="estadoConf2" readonly>
                        </div>
                        <div class="form-group">
                            <label>Dependencia del Usuario</label>
                            <input type="text" class="form-control" id="dependenciaConf2" readonly>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>¿Tiene TOKEN? <span style="color: red;">(*)</span></label>
                            <select name="tieneToken2" class="form-control" required>
                                <option value="">-- Seleccione --</option>
                                <option value="1">SIN TOKEN</option>
                                <option value="2">CON TOKEN</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Estado del TOKEN <span style="color: red;">(*)</span></label>
                            <select name="estadoToken2" class="form-control" 
                                    onchange="mostrarUnidad(this.value, '2')">
                                <option value="">-- Seleccione --</option>
                                <option value="1">OPERATIVO</option>
                                <option value="2">MALOGRADO</option>
                                <option value="3">PERDIDO</option>
                                <option value="4">ENTREGADO A UNIDAD ANTERIOR</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-row" id="rowUnidad2" style="display: none;">
                        <div class="form-group">
                            <label>Unidad a la que se entregó <span style="color: red;">(*)</span></label>
                            <select name="unidadEntrega2" class="form-control">
                                <option value="">-- Seleccione --</option>
                                <% if (dependencias != null) for (Dependencia dep : dependencias) { %>
                                <option value="<%= dep.getCoDependencia() %>"><%= dep.getDeDependencia() %></option>
                                <% } %>
                            </select>
                        </div>
                        <div class="form-group"></div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label>Doc Sustento final</label>
                            <input type="file" name="docSustentoFinal" class="form-control" 
                                   accept=".pdf,.jpg,.jpeg,.png">
                            <small class="form-hint">PDF, JPG, PNG (Máx. 5MB)</small>
                        </div>
                        <div class="form-group">
                            <label>Fecha de entrega <span style="color: red;">(*)</span></label>
                            <input type="date" name="fechaEntrega2" class="form-control" required>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group" style="grid-column: 1 / -1;">
                            <label>Observaciones</label>
                            <textarea name="observaciones2" class="form-control" rows="3"></textarea>
                        </div>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="cerrarModal('modalConfirmarToken')">
                        <i class="fas fa-times"></i> REGRESAR
                    </button>
                    <button type="submit" class="btn btn-success" id="btnGuardarConfirmacion">
                        <i class="fas fa-save"></i> GRABAR
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <!-- MODAL: VER DETALLE -->
    <div class="modal" id="modalDetalle">
        <div class="modal-content modal-large">
            <div class="modal-header">
                <h3><i class="fas fa-info-circle"></i> Detalle del Token</h3>
                <button class="modal-close" onclick="cerrarModal('modalDetalle')">&times;</button>
            </div>
            
            <div id="detalleContent" style="padding: 20px;">
                <!-- Se carga dinámicamente -->
            </div>
            
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="cerrarModal('modalDetalle')">
                    <i class="fas fa-times"></i> Cerrar
                </button>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/assets/js/dashboard.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/tokens.js"></script>
    <script>
        // Variable para pasar a JavaScript
        const esAdmin = <%= isAdmin %>;
    </script>
</body>
</html>
