package com.sinfa.token.dao;

import com.sinfa.token.config.DatabaseConfig;
import com.sinfa.token.model.Dependencia;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DependenciaDAO {
    
    /**
     * Listar dependencias activas de niveles 1-4
     */
    public List<Dependencia> listarActivas() {
        List<Dependencia> dependencias = new ArrayList<>();
        String sql = "SELECT co_dependencia, de_dependencia, co_nivel, in_baja " +
                    "FROM rhtm_dependencia " +
                    "WHERE in_baja = 0 AND co_nivel IN (1, 2, 3, 4) " +
                    "ORDER BY de_dependencia";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Dependencia dep = new Dependencia();
                dep.setCoDependencia(rs.getInt("co_dependencia"));
                dep.setDeDependencia(rs.getString("de_dependencia"));
                dep.setCoNivel(rs.getInt("co_nivel"));
                dep.setInBaja(rs.getInt("in_baja"));
                dependencias.add(dep);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al listar dependencias: " + e.getMessage());
        }
        
        return dependencias;
    }
    
    /**
     * Obtener dependencia por código
     */
    public Dependencia obtenerPorCodigo(int codigo) {
        Dependencia dependencia = null;
        String sql = "SELECT co_dependencia, de_dependencia, co_nivel, in_baja " +
                    "FROM rhtm_dependencia " +
                    "WHERE co_dependencia = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, codigo);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dependencia = new Dependencia();
                    dependencia.setCoDependencia(rs.getInt("co_dependencia"));
                    dependencia.setDeDependencia(rs.getString("de_dependencia"));
                    dependencia.setCoNivel(rs.getInt("co_nivel"));
                    dependencia.setInBaja(rs.getInt("in_baja"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener dependencia: " + e.getMessage());
        }
        
        return dependencia;
    }
    
    /**
     * Obtener código de dependencia del usuario en sesión
     */
    public int obtenerCodigoDependenciaUsuario(int codigoEmpleado) {
        String sql = "SELECT cemp_co_depend FROM rhtm_per_empleados WHERE cemp_co_emp = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, codigoEmpleado);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cemp_co_depend");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener dependencia del usuario: " + e.getMessage());
        }
        
        return 0;
    }
}