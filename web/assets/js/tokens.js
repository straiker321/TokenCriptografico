/**
 * tokens.js - Gestión de Tokens Criptográficos SINFA
 */

// ========== VARIABLES GLOBALES ==========
let tokenActual = null;

// ========== INICIALIZACIÓN ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('✓ tokens.js cargado correctamente');
    
    // Auto-ocultar alertas después de 5 segundos
    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(alert => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        });
    }, 5000);
    
    // Configurar validaciones
    configurarValidaciones();
    
    // Prevenir envío múltiple de formularios
    prevenirEnvioMultiple();
    
    // Configurar fecha máxima en campos de fecha
    configurarFechas();
});

// ========== MODALES ==========
function mostrarModal(idModal) {
    const modal = document.getElementById(idModal);
    if (modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
        console.log('✓ Modal abierto:', idModal);
    } else {
        console.error('✗ Modal no encontrado:', idModal);
    }
}

function cerrarModal(idModal) {
    const modal = document.getElementById(idModal);
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
        
        // Limpiar formulario
        const form = modal.querySelector('form');
        if (form) {
            form.reset();
            
            // Limpiar campos readonly
            form.querySelectorAll('input[readonly], input[type="text"]:disabled').forEach(input => {
                if (input.id) {
                    input.value = '';
                }
            });
            
            // Ocultar secciones opcionales
            const seccionConf2 = document.getElementById('seccionConfirmacion2');
            if (seccionConf2) {
                seccionConf2.style.display = 'none';
            }
            
            // Ocultar campos de unidad de entrega
            const rowUnidad1 = document.getElementById('rowUnidad1');
            const rowUnidad2 = document.getElementById('rowUnidad2');
            if (rowUnidad1) rowUnidad1.style.display = 'none';
            if (rowUnidad2) rowUnidad2.style.display = 'none';
        }
        
        console.log('✓ Modal cerrado:', idModal);
    }
}

// Cerrar modal al hacer clic fuera
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        const modals = ['modalAsignar', 'modalConfirmarToken', 'modalDetalle', 'modalEditarAdmin'];
        modals.forEach(modalId => {
            const modal = document.getElementById(modalId);
            if (modal && event.target === modal) {
                cerrarModal(modalId);
            }
        });
    }
}

// ========== BÚSQUEDA DE EMPLEADO (AJAX) ==========
function buscarEmpleado(dni, tipo) {
    console.log('Buscando empleado:', dni, 'tipo:', tipo);
    
    if (!dni || dni.length !== 8) {
        limpiarDatosEmpleado(tipo);
        return;
    }
    
    if (!/^\d{8}$/.test(dni)) {
        alert('El DNI debe contener exactamente 8 dígitos');
        limpiarDatosEmpleado(tipo);
        return;
    }
    
    let nombreId, estadoId, dependenciaId;
    
    if (tipo === 'asigna') {
        nombreId = 'nombreAsigna';
        estadoId = 'estadoAsigna';
        dependenciaId = 'dependenciaAsigna';
    } else if (tipo === 'editAsigna') {
        nombreId = 'nombreEditAsigna';
        estadoId = 'estadoEditAsigna';
        dependenciaId = 'dependenciaEditAsigna';
    } else if (tipo === 'conf1') {
        nombreId = 'nombreConf1';
        estadoId = 'estadoConf1';
        dependenciaId = 'dependenciaConf1';
    } else if (tipo === 'conf2') {
        nombreId = 'nombreConf2';
        estadoId = 'estadoConf2';
        dependenciaId = 'dependenciaConf2';
    } else {
        console.error('✗ Tipo no reconocido:', tipo);
        return;
    }
    
    const nombreInput = document.getElementById(nombreId);
    if (nombreInput) {
        nombreInput.value = 'Buscando...';
    }
    
    // Realizar búsqueda AJAX
    fetch('tokens?action=buscarEmpleado&dni=' + dni)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la búsqueda');
            }
            return response.json();
        })
        .then(data => {
            console.log('Datos recibidos:', data);
            
            if (data.error) {
                alert(data.error);
                limpiarDatosEmpleado(tipo);
            } else {
                // Mostrar datos
                if (nombreInput) nombreInput.value = data.nombre || '';
                
                const estadoInput = document.getElementById(estadoId);
                if (estadoInput) estadoInput.value = data.estado || '';
                
                const dependenciaInput = document.getElementById(dependenciaId);
                if (dependenciaInput) dependenciaInput.value = data.dependencia || '';
                
                console.log('✓ Datos del empleado cargados');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al buscar empleado. Intente nuevamente.');
            limpiarDatosEmpleado(tipo);
        });
}

function limpiarDatosEmpleado(tipo) {
    let nombreId, estadoId, dependenciaId;
    
    if (tipo === 'asigna') {
        nombreId = 'nombreAsigna';
        estadoId = 'estadoAsigna';
        dependenciaId = 'dependenciaAsigna';
    } else if (tipo === 'editAsigna') {
        nombreId = 'nombreEditAsigna';
        estadoId = 'estadoEditAsigna';
        dependenciaId = 'dependenciaEditAsigna';
    } else if (tipo === 'conf1') {
        nombreId = 'nombreConf1';
        estadoId = 'estadoConf1';
        dependenciaId = 'dependenciaConf1';
    } else if (tipo === 'conf2') {
        nombreId = 'nombreConf2';
        estadoId = 'estadoConf2';
        dependenciaId = 'dependenciaConf2';
    }
    
    const nombreInput = document.getElementById(nombreId);
    const estadoInput = document.getElementById(estadoId);
    const dependenciaInput = document.getElementById(dependenciaId);
    
    if (nombreInput) nombreInput.value = '';
    if (estadoInput) estadoInput.value = '';
    if (dependenciaInput) dependenciaInput.value = '';
}

// ========== EDITAR/CONFIRMAR TOKEN ==========
function editarToken(id) {
    console.log('Editando token:', id);
    tokenActual = id;
    
    // Cargar datos del token
    fetch('tokens?action=getToken&id=' + id)
        .then(response => response.json())
        .then(data => {
            console.log('Token recibido:', data);
            
            if (data.error) {
                alert(data.error);
                return;
            }
            
            // Si es ADMIN, mostrar formulario de edición completo
            if (typeof esAdmin !== 'undefined' && esAdmin) {
                mostrarFormularioEdicionAdmin(id, data);
                return;
            }
            
            // NO ADMIN: Mostrar formulario de confirmación
            mostrarFormularioConfirmacion(id, data);
        })
        .catch(error => {
            console.error('Error al cargar token:', error);
            alert('Error al cargar datos del token');
        });
}

function mostrarFormularioEdicionAdmin(id, data) {
    console.log('→ Mostrando formulario de EDICIÓN ADMIN');
    console.log('Datos recibidos completos:', data);
    
    // Llenar datos del formulario de edición
    document.getElementById('idTokenEditar').value = id;
    
    // Unidad registra
    if (data.codigoUnidad) {
        document.getElementById('unidadRegistraEdit').value = data.codigoUnidad;
        console.log('  - Unidad registra:', data.codigoUnidad);
    }
    
    // Acción
    if (data.tipoAccion) {
        document.getElementById('accionEdit').value = data.tipoAccion;
        console.log('  - Tipo acción:', data.tipoAccion);
    }
    
    // DNI asignado
    document.getElementById('dniAsignaEdit').value = data.dni || '';
    document.getElementById('nombreEditAsigna').value = data.nombre || '';
    document.getElementById('estadoEditAsigna').value = data.estado || '';
    document.getElementById('dependenciaEditAsigna').value = data.dependencia || '';
    
    // DNI que recibe
    document.getElementById('dniRecibeEdit').value = data.dniRecibe || '';
    
    // Documento sustento actual
    document.getElementById('docSustentoActual').value = data.docSustento || 'Sin archivo';
    
    // Fecha de acción - Convertir si viene en formato ISO
    if (data.fechaAccion) {
        try {
            // Si ya viene en formato yyyy-MM-dd, usar directamente
            if (data.fechaAccion.match(/^\d{4}-\d{2}-\d{2}$/)) {
                document.getElementById('fechaAccionEdit').value = data.fechaAccion;
            } else {
                // Si viene en otro formato, intentar convertir
                const fecha = new Date(data.fechaAccion);
                const fechaFormateada = fecha.toISOString().split('T')[0];
                document.getElementById('fechaAccionEdit').value = fechaFormateada;
            }
            console.log('  - Fecha acción:', data.fechaAccion);
        } catch (e) {
            console.error('Error al parsear fecha:', e);
            document.getElementById('fechaAccionEdit').value = data.fechaAccion || '';
        }
    }
    
    console.log('✓ Formulario de edición ADMIN llenado');
    
    // Mostrar modal
    mostrarModal('modalEditarAdmin');
}

function eliminarTokenDesdeModal() {
    if (tokenActual) {
        if (confirm('¿Está seguro de eliminar este token?\n\nEsta acción no se puede deshacer.')) {
            cerrarModal('modalEditarAdmin');
            window.location.href = 'tokens?action=delete&id=' + tokenActual;
        }
    }
}

function mostrarFormularioConfirmacion(id, data) {
    console.log('Mostrando formulario de confirmación');
    
    // Llenar SECCIÓN 1: Datos del token registrado (solo lectura)
    document.getElementById('idTokenConfirmar').value = id;
    document.getElementById('dniTokenReg').value = data.dni || '';
    document.getElementById('nombreTokenReg').value = data.nombre || '';
    document.getElementById('estadoTokenReg').value = data.estado || '';
    document.getElementById('dependenciaTokenReg').value = data.dependencia || '';
    document.getElementById('accionTokenReg').value = data.accion || '';
    document.getElementById('docSustentoReg').value = data.docSustento || '';
    
    // Convertir fecha si viene en formato ISO
    if (data.fechaAccion) {
        try {
            const fecha = new Date(data.fechaAccion);
            const fechaFormateada = fecha.toISOString().split('T')[0];
            document.getElementById('fechaAccionReg').value = fechaFormateada;
        } catch (e) {
            document.getElementById('fechaAccionReg').value = data.fechaAccion;
        }
    }
    
    const seccionInicial = document.getElementById('seccionConfirmacionInicial');
    const seccionFinal = document.getElementById('seccionConfirmacionFinal');
    
    // Determinar qué mostrar según el estado del token
    const tieneInicial = data.codempcon !== undefined && data.codempcon !== null && data.codempcon > 0;
    const tieneFinal = data.codempcon2 !== undefined && data.codempcon2 !== null && data.codempcon2 > 0;
    
    console.log('Confirmacion Inicial:', tieneInicial);
    console.log('Confirmacion Final:', tieneFinal);
    
    if (!tieneInicial) {
        // PRIMERA CONFIRMACIÓN
        console.log('→ Mostrando formulario de Confirmacion Inicial');
        
        document.getElementById('actionConfirmar').value = 'confirmar1';
        seccionInicial.style.display = 'block';
        seccionFinal.style.display = 'none';
        
        // Habilitar todos los campos de confirmación 1
        seccionInicial.querySelectorAll('input, select, textarea').forEach(el => {
            if (!el.readOnly && el.type !== 'button') {
                el.disabled = false;
            }
        });
        
        // Limpiar campos
        seccionInicial.querySelectorAll('input:not([readonly]), select, textarea').forEach(el => {
            if (el.type !== 'file' && el.type !== 'button') {
                el.value = '';
            }
        });
        
        // Marcar campos requeridos
        seccionInicial.querySelectorAll('[required]').forEach(el => el.required = true);
        seccionFinal.querySelectorAll('[required]').forEach(el => el.required = false);
        
    } else if (tieneInicial && !tieneFinal) {
        // SEGUNDA CONFIRMACIÓN
        console.log('→ Mostrando formulario de Confirmacion Final');
        
        document.getElementById('actionConfirmar').value = 'confirmar2';
        seccionInicial.style.display = 'block';
        seccionFinal.style.display = 'block';
        
        // Deshabilitar todos los campos de confirmación 1
        seccionInicial.querySelectorAll('input, select, textarea').forEach(el => {
            el.disabled = true;
        });
        seccionInicial.querySelectorAll('[required]').forEach(el => el.required = false);
        
        // Mostrar datos de la primera confirmación en resumen
        document.getElementById('datoDniConf1').textContent = data.dniConf1 || '-';
        document.getElementById('datoTieneConf1').textContent = data.tieneTokenConf1 || '-';
        document.getElementById('datoEstadoConf1').textContent = data.estadoTokenConf1 || '-';
        
       
        // Habilitar campos de confirmación 2
        seccionFinal.querySelectorAll('input, select, textarea').forEach(el => {
            if (!el.readOnly && el.type !== 'button') {
                el.disabled = false;
            }
        });
        
        // Limpiar campos
        seccionFinal.querySelectorAll('input:not([readonly]), select, textarea').forEach(el => {
            if (el.type !== 'file' && el.type !== 'button') {
                el.value = '';
            }
        });
        
        // Marcar campos requeridos
        seccionFinal.querySelectorAll('[required]').forEach(el => el.required = true);
        
    } else {
        alert('Este token ya tiene ambas confirmaciones registradas');
        return;
    }
    
    mostrarModal('modalConfirmarToken');
}

// ========== VER DETALLE ==========
function verDetalle(id) {
    console.log('Viendo detalle del token:', id);
    
    fetch('tokens?action=getToken&id=' + id)
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                alert(data.error);
                return;
            }
            
            let html = '<div class="detalle-info">';
            
            // Información del Usuario
            html += '<div class="detalle-section">';
            html += '<h4><i class="fas fa-user"></i> Información del Usuario</h4>';
            html += '<p><strong>DNI:</strong> ' + (data.dni || 'N/A') + '</p>';
            html += '<p><strong>Nombre:</strong> ' + (data.nombre || 'N/A') + '</p>';
            html += '<p><strong>Dependencia:</strong> ' + (data.dependencia || 'N/A') + '</p>';
            html += '<p><strong>Acción:</strong> ' + (data.accion || 'N/A') + '</p>';
            
            if (data.docSustento) {
                html += '<p><strong>Documento:</strong> <a href="tokens?action=download&file=' + 
                        encodeURIComponent(data.docSustento) + '" target="_blank" class="btn-link">' +
                        '<i class="fas fa-file-pdf"></i> Ver archivo</a></p>';
            }
            html += '</div>';
            
            // Primera Confirmación
            if (data.dniConf1) {
                html += '<div class="detalle-section">';
                html += '<h4><i class="fas fa-check-circle"></i> Primera Confirmación</h4>';
                html += '<p><strong>DNI:</strong> ' + data.dniConf1 + '</p>';
                html += '<p><strong>Tiene Token:</strong> ' + (data.tieneTokenConf1 || 'N/A') + '</p>';
                html += '<p><strong>Estado:</strong> ' + (data.estadoTokenConf1 || 'N/A') + '</p>';
                html += '</div>';
            }
            
            // Segunda Confirmación
            if (data.dniConf2) {
                html += '<div class="detalle-section">';
                html += '<h4><i class="fas fa-check-double"></i> Segunda Confirmación</h4>';
                html += '<p><strong>DNI:</strong> ' + data.dniConf2 + '</p>';
                html += '<p><strong>Tiene Token:</strong> ' + (data.tieneTokenConf2 || 'N/A') + '</p>';
                html += '<p><strong>Estado:</strong> ' + (data.estadoTokenConf2 || 'N/A') + '</p>';
                html += '</div>';
            }
            
            html += '</div>';
            
            document.getElementById('detalleContent').innerHTML = html;
            mostrarModal('modalDetalle');
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al cargar detalles del token');
        });
}

// ========== ELIMINAR TOKEN ==========
function eliminarToken(id) {
    if (confirm('¿Está seguro de eliminar este token?\n\nEsta acción no se puede deshacer.')) {
        console.log('Eliminando token:', id);
        window.location.href = 'tokens?action=delete&id=' + id;
    }
}

// ========== MOSTRAR/OCULTAR UNIDAD DE ENTREGA ==========
function mostrarUnidad(estado, tipo) {
    const row = document.getElementById('rowUnidad' + tipo);
    if (!row) {
        console.warn('No se encontró rowUnidad' + tipo);
        return;
    }
    
    const select = row.querySelector('select');
    
    if (estado == '4') { // ENTREGADO A UNIDAD ANTERIOR
        row.style.display = 'grid';
        if (select) select.required = true;
        console.log('✓ Campo de unidad de entrega mostrado');
    } else {
        row.style.display = 'none';
        if (select) {
            select.required = false;
            select.value = '';
        }
    }
}

// ========== VER ARCHIVO ==========
function verArchivoModal(campoId) {
    const campo = document.getElementById(campoId);
    if (!campo) {
        console.error('Campo no encontrado:', campoId);
        return;
    }
    
    const archivo = campo.value;
    if (!archivo || archivo === 'Sin archivo') {
        alert('No hay archivo disponible');
        return;
    }
    
    const url = 'tokens?action=download&file=' + encodeURIComponent(archivo);
    console.log('Abriendo archivo:', url);
    window.open(url, '_blank');
}

// ========== EXPORTAR PDF ==========
function exportarPDF() {
    console.log('Generando PDF...');
    
    const tabla = document.querySelector('.tokens-table');
    if (!tabla) {
        alert('No hay datos para exportar');
        return;
    }
    
    const filas = tabla.querySelectorAll('tbody tr');
    if (filas.length === 0 || (filas.length === 1 && filas[0].cells.length === 1)) {
        alert('No hay tokens para exportar');
        return;
    }
    
    // Crear ventana de impresión
    const ventanaImpresion = window.open('', '_blank', 'width=800,height=600');
    
    let contenidoHTML = `
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>Reporte de Tokens - SINFA</title>
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body { font-family: Arial, sans-serif; padding: 20px; font-size: 11px; }
                .header { text-align: center; margin-bottom: 20px; border-bottom: 2px solid #1e3a8a; padding-bottom: 15px; }
                .header h1 { color: #1e3a8a; font-size: 20px; margin-bottom: 5px; }
                .header p { color: #666; font-size: 12px; }
                .info { margin-bottom: 15px; font-size: 10px; }
                table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                th { background-color: #1e3a8a; color: white; padding: 8px 6px; text-align: left; font-size: 10px; }
                td { padding: 6px; border-bottom: 1px solid #ddd; font-size: 10px; }
                tr:nth-child(even) { background-color: #f9f9f9; }
                .badge { display: inline-block; padding: 2px 6px; border-radius: 3px; font-size: 8px; font-weight: bold; color: white; }
                .badge-success { background-color: #059669; }
                .badge-warning { background-color: #f59e0b; }
                .badge-info { background-color: #3b82f6; }
                .footer { margin-top: 20px; text-align: center; font-size: 9px; color: #666; border-top: 1px solid #ddd; padding-top: 10px; }
                @media print {
                    .no-print { display: none !important; }
                    @page { margin: 1.5cm; }
                }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>🔐 SISTEMA DE GESTIÓN DE TOKENS CRIPTOGRÁFICOS - SINFA</h1>
                <p>Reporte de Asignación de Tokens</p>
            </div>
            
            <div class="info">
                <p><strong>Fecha de generación:</strong> ${new Date().toLocaleDateString('es-PE', {
                    year: 'numeric', month: 'long', day: 'numeric', 
                    hour: '2-digit', minute: '2-digit'
                })}</p>
                <p><strong>Total de registros:</strong> ${filas.length}</p>
            </div>
            
            <table>
                <thead>
                    <tr>
                        <th>N°</th>
                        <th>Fecha</th>
                        <th>DNI</th>
                        <th>Usuario</th>
                        <th>Dependencia</th>
                        <th>Estado</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
    `;
    
    // Agregar filas
    filas.forEach((fila, index) => {
        if (fila.cells.length > 1) {
            const celdas = fila.cells;
            contenidoHTML += '<tr>';
            for (let i = 0; i < celdas.length - 1; i++) {
                if (i === 5) { // Columna estado
                    const badge = celdas[i].querySelector('.badge');
                    if (badge) {
                        contenidoHTML += `<td><span class="${badge.className}">${badge.textContent}</span></td>`;
                    } else {
                        contenidoHTML += `<td>${celdas[i].textContent.trim()}</td>`;
                    }
                } else {
                    contenidoHTML += `<td>${celdas[i].textContent.trim()}</td>`;
                }
            }
            contenidoHTML += '</tr>';
        }
    });
    
    contenidoHTML += `
                </tbody>
            </table>
            
            <div class="footer">
                <p>Sistema Integrado de Administración Financiera - SINFA</p>
                <p>Este documento ha sido generado automáticamente el ${new Date().toLocaleString('es-PE')}</p>
            </div>
            
            <div class="no-print" style="text-align: center; margin-top: 20px; padding: 20px; background: #f3f4f6; border-radius: 8px;">
                <button onclick="window.print()" style="padding: 12px 24px; font-size: 14px; cursor: pointer; background: linear-gradient(135deg, #3b82f6 0%, #1e3a8a 100%); color: white; border: none; border-radius: 6px; margin-right: 10px; font-weight: 600;">
                    <i class="fas fa-print"></i> Imprimir / Guardar como PDF
                </button>
                <button onclick="window.close()" style="padding: 12px 24px; font-size: 14px; cursor: pointer; background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%); color: white; border: none; border-radius: 6px; font-weight: 600;">
                    <i class="fas fa-times"></i> Cerrar
                </button>
            </div>
        </body>
        </html>
    `;
    
    ventanaImpresion.document.write(contenidoHTML);
    ventanaImpresion.document.close();
    
    ventanaImpresion.onload = function() {
        setTimeout(() => {
            ventanaImpresion.focus();
        }, 300);
    };
    
    console.log('✓ PDF generado correctamente');
}

// ========== VALIDACIONES ==========
function configurarValidaciones() {
    // Validar DNI solo números
    const dniInputs = document.querySelectorAll('input[pattern="[0-9]{8}"]');
    dniInputs.forEach(input => {
        input.addEventListener('input', function(e) {
            this.value = this.value.replace(/[^0-9]/g, '');
            if (this.value.length > 8) {
                this.value = this.value.slice(0, 8);
            }
        });
    });
    
    // Validar archivos
    const fileInputs = document.querySelectorAll('input[type="file"]');
    fileInputs.forEach(input => {
        input.addEventListener('change', function(e) {
            const file = this.files[0];
            if (file) {
                const maxSize = 5 * 1024 * 1024; // 5MB
                const allowedTypes = ['application/pdf', 'image/jpeg', 'image/jpg', 'image/png'];
                
                if (file.size > maxSize) {
                    alert('El archivo no debe superar los 5MB');
                    this.value = '';
                    return;
                }
                
                if (!allowedTypes.includes(file.type)) {
                    alert('Solo se permiten archivos PDF, JPG o PNG');
                    this.value = '';
                    return;
                }
                
                console.log(`✓ Archivo válido: ${file.name} (${(file.size/1024/1024).toFixed(2)} MB)`);
            }
        });
    });
}

function configurarFechas() {
    const dateInputs = document.querySelectorAll('input[type="date"]');
    const today = new Date().toISOString().split('T')[0];
    
    dateInputs.forEach(input => {
        if (!input.hasAttribute('min') && !input.readOnly) {
            input.max = today;
        }
    });
}

function prevenirEnvioMultiple() {
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitBtn = this.querySelector('button[type="submit"]');
            
            if (submitBtn && submitBtn.disabled) {
                e.preventDefault();
                return false;
            }
            
            if (submitBtn) {
                submitBtn.disabled = true;
                const originalHTML = submitBtn.innerHTML;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';
                
                setTimeout(() => {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = originalHTML;
                }, 5000);
            }
        });
    });
}

/**
 * Validar formulario de búsqueda
 * Requiere al menos DNI O rango de fechas
 */
function validarBusqueda() {
    const dni = document.getElementById('dniBusqueda').value.trim();
    const fechaDesde = document.getElementById('fechaDesde').value;
    const fechaHasta = document.getElementById('fechaHasta').value;
    
    // Validar que se ingresó al menos un criterio
    if (!dni && !fechaDesde && !fechaHasta) {
        alert('⚠️ CRITERIO DE BÚSQUEDA REQUERIDO\n\n' +
              'Debe ingresar al menos uno de los siguientes:\n' +
              '• DNI (8 dígitos)\n' +
              '• Rango de fechas (desde - hasta)\n\n' +
              'Para ver todos los tokens, use el botón "Limpiar".');
        return false;
    }
    
    // Si hay DNI, validar formato
    if (dni) {
        if (!/^\d{8}$/.test(dni)) {
            alert('⚠️ DNI INVÁLIDO\n\n' +
                  'El DNI debe contener exactamente 8 dígitos numéricos.\n\n' +
                  'Ejemplo: 12345678');
            document.getElementById('dniBusqueda').focus();
            return false;
        }
    }
    
    // Si hay fechas, validar que estén ambas
    if ((fechaDesde && !fechaHasta) || (!fechaDesde && fechaHasta)) {
        alert('⚠️ RANGO DE FECHAS INCOMPLETO\n\n' +
              'Debe ingresar ambas fechas:\n' +
              '• Fecha desde\n' +
              '• Fecha hasta');
        return false;
    }
    
    // Si hay ambas fechas, validar que "desde" sea menor que "hasta"
    if (fechaDesde && fechaHasta) {
        const desde = new Date(fechaDesde);
        const hasta = new Date(fechaHasta);
        
        if (desde > hasta) {
            alert('⚠️ RANGO DE FECHAS INVÁLIDO\n\n' +
                  'La "Fecha desde" no puede ser mayor que "Fecha hasta".\n\n' +
                  'Por favor corrija las fechas.');
            return false;
        }
    }
    
    // Todo OK - permitir envío
    console.log('✓ Búsqueda válida:', { dni, fechaDesde, fechaHasta });
    return true;
}

// ========== LOG ==========
console.log('✓✓✓ tokens.js CORREGIDO completamente cargado y funcional ✓✓✓');