package com.sinfa.token.dao;

import com.sinfa.token.config.DatabaseConfig;
import com.sinfa.token.model.Usuario;
import java.sql.*;


public class UsuarioDAO {
    

    public Usuario autenticar(String username, String password) {
        Usuario usuario = null;
        String sql = "SELECT u.id_usuario, u.username, u.cemp_co_emp, u.id_perfil, " +
                    "u.activo, u.ultimo_acceso, u.created_at, " +
                    "e.cemp_apepat || ' ' || e.cemp_apemat || ' ' || e.cemp_denom as nombre_completo, " +
                    "p.nombre_perfil, " +
                    "d.de_dependencia " +
                    "FROM sgd_usuarios u " +
                    "INNER JOIN rhtm_per_empleados e ON u.cemp_co_emp = e.cemp_co_emp " +
                    "INNER JOIN sgd_perfiles p ON u.id_perfil = p.id_perfil " +
                    "INNER JOIN rhtm_dependencia d ON e.cemp_co_depend = d.co_dependencia " +
                    "WHERE u.username = ? AND u.password = ? AND u.activo = 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setCempCoEmp(rs.getInt("cemp_co_emp"));
                    usuario.setIdPerfil(rs.getInt("id_perfil"));
                    usuario.setActivo(rs.getInt("activo"));
                    usuario.setUltimoAcceso(rs.getTimestamp("ultimo_acceso"));
                    usuario.setCreatedAt(rs.getTimestamp("created_at"));
                    usuario.setNombreCompleto(rs.getString("nombre_completo"));
                    usuario.setNombrePerfil(rs.getString("nombre_perfil"));
                    usuario.setDependencia(rs.getString("de_dependencia"));
                    
                    // Actualizar último acceso
                    actualizarUltimoAcceso(usuario.getIdUsuario());
                    
                    System.out.println("✓ Login exitoso: " + username + " - " + usuario.getNombrePerfil());
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error en autenticación: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuario;
    }
    

    private void actualizarUltimoAcceso(int idUsuario) {
        String sql = "UPDATE sgd_usuarios SET ultimo_acceso = CURRENT_TIMESTAMP WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar último acceso: " + e.getMessage());
        }
    }
    

    public Usuario obtenerPorId(int idUsuario) {
        Usuario usuario = null;
        String sql = "SELECT u.id_usuario, u.username, u.cemp_co_emp, u.id_perfil, " +
                    "u.activo, u.ultimo_acceso, u.created_at, " +
                    "e.cemp_apepat || ' ' || e.cemp_apemat || ' ' || e.cemp_denom as nombre_completo, " +
                    "p.nombre_perfil, " +
                    "d.de_dependencia " +
                    "FROM sgd_usuarios u " +
                    "INNER JOIN rhtm_per_empleados e ON u.cemp_co_emp = e.cemp_co_emp " +
                    "INNER JOIN sgd_perfiles p ON u.id_perfil = p.id_perfil " +
                    "INNER JOIN rhtm_dependencia d ON e.cemp_co_depend = d.co_dependencia " +
                    "WHERE u.id_usuario = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setCempCoEmp(rs.getInt("cemp_co_emp"));
                    usuario.setIdPerfil(rs.getInt("id_perfil"));
                    usuario.setActivo(rs.getInt("activo"));
                    usuario.setUltimoAcceso(rs.getTimestamp("ultimo_acceso"));
                    usuario.setCreatedAt(rs.getTimestamp("created_at"));
                    usuario.setNombreCompleto(rs.getString("nombre_completo"));
                    usuario.setNombrePerfil(rs.getString("nombre_perfil"));
                    usuario.setDependencia(rs.getString("de_dependencia"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuario;
    }
    

    public boolean existeUsername(String username) {
        String sql = "SELECT COUNT(*) FROM sgd_usuarios WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al verificar username: " + e.getMessage());
        }
        
        return false;
    }
    

    public boolean cambiarPassword(int idUsuario, String nuevaPassword) {
        String sql = "UPDATE sgd_usuarios SET password = ? WHERE id_usuario = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nuevaPassword);
            ps.setInt(2, idUsuario);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Error al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }
}