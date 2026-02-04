/**
 * Dashboard JavaScript
 * Sistema de Gestión de Tokens Criptográficos
 */

// Variables globales
let sidebarCollapsed = false;

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('✓ Dashboard cargado correctamente');
    
    // Inicializar componentes
    initializeSidebar();
    initializeClock();
    initializeUserMenu();
    loadStatistics();
    loadRecentActivity();
    
    // Marcar item activo en el menú
    markActiveMenuItem();
});

/**
 * Inicializar sidebar
 */
function initializeSidebar() {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    
    // Verificar si el sidebar estaba colapsado (localStorage)
    const wasCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
    
    if (wasCollapsed) {
        sidebar.classList.add('collapsed');
        mainContent.classList.add('expanded');
        sidebarCollapsed = true;
    }
    
    // Agregar event listeners a los nav items
    const navItems = document.querySelectorAll('.nav-item a');
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            // Si es móvil, colapsar sidebar al hacer clic
            if (window.innerWidth <= 1024) {
                toggleSidebar();
            }
        });
    });
}

/**
 * Toggle sidebar (colapsar/expandir)
 */
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    
    sidebar.classList.toggle('collapsed');
    mainContent.classList.toggle('expanded');
    
    sidebarCollapsed = !sidebarCollapsed;
    
    // Guardar estado en localStorage
    localStorage.setItem('sidebarCollapsed', sidebarCollapsed);
}

/**
 * Inicializar reloj en tiempo real
 */
function initializeClock() {
    updateClock();
    setInterval(updateClock, 1000);
}

function updateClock() {
    const clockElement = document.getElementById('currentTime');
    if (!clockElement) return;
    
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    
    clockElement.textContent = `${hours}:${minutes}:${seconds}`;
}

/**
 * Inicializar menú de usuario
 */
function initializeUserMenu() {
    // Cerrar dropdown al hacer clic fuera
    document.addEventListener('click', function(event) {
        const userMenu = document.querySelector('.user-menu');
        const dropdown = document.getElementById('userDropdown');
        
        if (!userMenu || !dropdown) return;
        
        if (!userMenu.contains(event.target)) {
            dropdown.classList.remove('show');
        }
    });
}

/**
 * Toggle user menu dropdown
 */
function toggleUserMenu() {
    const dropdown = document.getElementById('userDropdown');
    if (dropdown) {
        dropdown.classList.toggle('show');
    }
}

/**
 * Cargar estadísticas del dashboard
 */
function loadStatistics() {
    // Simular carga de estadísticas con animación
    setTimeout(() => {
        animateNumber('totalTokens', 0, 156, 1000);
        animateNumber('tokensOperativos', 0, 142, 1200);
        animateNumber('tokensPendientes', 0, 8, 1400);
        animateNumber('tokensProblemas', 0, 6, 1600);
    }, 300);
}

/**
 * Animar números con efecto contador
 */
function animateNumber(elementId, start, end, duration) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    const range = end - start;
    const increment = range / (duration / 16); // 60 FPS
    let current = start;
    
    const timer = setInterval(() => {
        current += increment;
        if (current >= end) {
            current = end;
            clearInterval(timer);
        }
        element.textContent = Math.floor(current);
    }, 16);
}

/**
 * Cargar actividad reciente
 */
function loadRecentActivity() {
    // En producción, esto se cargaría via AJAX desde el servidor
    const activityList = document.getElementById('activityList');
    if (!activityList) return;
    
    const activities = [
        {
            icon: 'fa-check',
            iconClass: 'activity-success',
            title: 'Token asignado exitosamente',
            time: 'Hace 2 horas'
        },
        {
            icon: 'fa-edit',
            iconClass: 'activity-info',
            title: 'Confirmación de entrega registrada',
            time: 'Hace 3 horas'
        },
        {
            icon: 'fa-exclamation-triangle',
            iconClass: 'activity-warning',
            title: 'Token reportado como malogrado',
            time: 'Hace 5 horas'
        }
    ];
    
    // Limpiar lista actual
    activityList.innerHTML = '';
    
    // Agregar actividades
    activities.forEach(activity => {
        const item = createActivityItem(activity);
        activityList.appendChild(item);
    });
}

/**
 * Crear elemento de actividad
 */
function createActivityItem(activity) {
    const div = document.createElement('div');
    div.className = 'activity-item';
    div.innerHTML = `
        <div class="activity-icon ${activity.iconClass}">
            <i class="fas ${activity.icon}"></i>
        </div>
        <div class="activity-content">
            <p class="activity-title">${activity.title}</p>
            <p class="activity-time">${activity.time}</p>
        </div>
    `;
    return div;
}

/**
 * Marcar item activo en el menú según la página actual
 */
function markActiveMenuItem() {
    const currentPath = window.location.pathname;
    const navItems = document.querySelectorAll('.nav-item');
    
    navItems.forEach(item => {
        const link = item.querySelector('a');
        if (link) {
            const href = link.getAttribute('href');
            if (currentPath.includes(href)) {
                navItems.forEach(i => i.classList.remove('active'));
                item.classList.add('active');
            }
        }
    });
}

/**
 * Cerrar sesión con confirmación
 */
function confirmarCerrarSesion() {
    if (confirm('¿Está seguro que desea cerrar sesión?')) {
        window.location.href = 'logout';
    }
    return false;
}

/**
 * Mostrar notificación
 */
function showNotification(message, type = 'info') {
    // Crear elemento de notificación
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <i class="fas fa-${getIconForType(type)}"></i>
        <span>${message}</span>
        <button onclick="this.parentElement.remove()">&times;</button>
    `;
    
    // Agregar al body
    document.body.appendChild(notification);
    
    // Animar entrada
    setTimeout(() => notification.classList.add('show'), 10);
    
    // Auto-remover después de 5 segundos
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 5000);
}

/**
 * Obtener icono según tipo de notificación
 */
function getIconForType(type) {
    const icons = {
        'success': 'check-circle',
        'error': 'exclamation-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

/**
 * Formatear fecha
 */
function formatDate(date) {
    const d = new Date(date);
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    return `${day}/${month}/${year}`;
}

/**
 * Formatear hora
 */
function formatTime(date) {
    const d = new Date(date);
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
}

/**
 * Responsive: Ajustar sidebar en móvil
 */
window.addEventListener('resize', function() {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    
    if (window.innerWidth <= 1024) {
        // En móvil, sidebar colapsado por defecto
        if (!sidebar.classList.contains('collapsed')) {
            sidebar.classList.add('collapsed');
        }
    }
});

/**
 * Prevenir cierre accidental de página
 */
window.addEventListener('beforeunload', function(e) {
    // Solo si hay cambios sin guardar
    const hasUnsavedChanges = false; // Implementar lógica según necesidad
    
    if (hasUnsavedChanges) {
        e.preventDefault();
        e.returnValue = '';
    }
});

// Exportar funciones globales
window.toggleSidebar = toggleSidebar;
window.toggleUserMenu = toggleUserMenu;
window.confirmarCerrarSesion = confirmarCerrarSesion;
window.showNotification = showNotification;
window.formatDate = formatDate;
window.formatTime = formatTime;

console.log('✓ JavaScript del dashboard inicializado correctamente');