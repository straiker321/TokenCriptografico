package com.sinfa.token.dao;

import com.sinfa.token.config.DatabaseConfig;
import com.sinfa.token.model.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {
    
 
    public Empleado buscarPorDNI(String dni) {
        Empleado empleado = null;
        String sql = "SELECT e.cemp_co_emp, e.cemp_nu_dni, e.cemp_denom, " +
                    "e.cemp_apepat, e.cemp_apemat, e.cemp_co_depend, e.cemp_indbaj, " +
                    "CASE WHEN e.cemp_indbaj = 0 THEN 'DE BAJA' ELSE 'ACTIVO' END as estado_texto, " +
                    "d.de_dependencia " +
                    "FROM rhtm_per_empleados e " +
                    "INNER JOIN rhtm_dependencia d ON e.cemp_co_depend = d.co_dependencia " +
                    "WHERE e.cemp_nu_dni = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, dni);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    empleado = new Empleado();
                    empleado.setCempCoEmp(rs.getInt("cemp_co_emp"));
                    empleado.setCempNuDni(rs.getString("cemp_nu_dni"));
                    empleado.setCempDenom(rs.getString("cemp_denom"));
                    empleado.setCempApepat(rs.getString("cemp_apepat"));
                    empleado.setCempApemat(rs.getString("cemp_apemat"));
                    empleado.setCempCoDepend(rs.getInt("cemp_co_depend"));
                    empleado.setCempIndbaj(rs.getInt("cemp_indbaj"));
                    empleado.setEstadoTexto(rs.getString("estado_texto"));
                    empleado.setDependencia(rs.getString("de_dependencia"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al buscar empleado por DNI: " + e.getMessage());
            e.printStackTrace();
        }
        
        return empleado;
    }
    
    /**
     * Contar registros existentes para un DNI
     */
    public int contarRegistrosPorDNI(String dni) {
        String sql = "SELECT COUNT(*) FROM t_m_asigna_token WHERE numdnitok = ? AND estado = 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, dni);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al contar registros: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Contar registros de tokens recibidos para un DNI
     */
    public int contarRegistrosRecibidosPorDNI(String dni) {
        String sql = "SELECT COUNT(*) FROM t_m_asigna_token WHERE dniemprec = ? AND estado = 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, dni);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al contar tokens recibidos: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Obtener empleado por código
     */
    public Empleado obtenerPorCodigo(int codigo) {
        Empleado empleado = null;
        String sql = "SELECT e.cemp_co_emp, e.cemp_nu_dni, e.cemp_denom, " +
                    "e.cemp_apepat, e.cemp_apemat, e.cemp_co_depend, e.cemp_indbaj, " +
                    "d.de_dependencia " +
                    "FROM rhtm_per_empleados e " +
                    "INNER JOIN rhtm_dependencia d ON e.cemp_co_depend = d.co_dependencia " +
                    "WHERE e.cemp_co_emp = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, codigo);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    empleado = new Empleado();
                    empleado.setCempCoEmp(rs.getInt("cemp_co_emp"));
                    empleado.setCempNuDni(rs.getString("cemp_nu_dni"));
                    empleado.setCempDenom(rs.getString("cemp_denom"));
                    empleado.setCempApepat(rs.getString("cemp_apepat"));
                    empleado.setCempApemat(rs.getString("cemp_apemat"));
                    empleado.setCempCoDepend(rs.getInt("cemp_co_depend"));
                    empleado.setCempIndbaj(rs.getInt("cemp_indbaj"));
                    empleado.setDependencia(rs.getString("de_dependencia"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener empleado: " + e.getMessage());
        }
        
        return empleado;
    }
    
    /**
     * Listar todos los empleados activos
     */
    public List<Empleado> listarActivos() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT e.cemp_co_emp, e.cemp_nu_dni, e.cemp_denom, " +
                    "e.cemp_apepat, e.cemp_apemat, e.cemp_co_depend, e.cemp_indbaj, " +
                    "d.de_dependencia " +
                    "FROM rhtm_per_empleados e " +
                    "INNER JOIN rhtm_dependencia d ON e.cemp_co_depend = d.co_dependencia " +
                    "WHERE e.cemp_indbaj = 1 " +
                    "ORDER BY e.cemp_apepat, e.cemp_apemat, e.cemp_denom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setCempCoEmp(rs.getInt("cemp_co_emp"));
                empleado.setCempNuDni(rs.getString("cemp_nu_dni"));
                empleado.setCempDenom(rs.getString("cemp_denom"));
                empleado.setCempApepat(rs.getString("cemp_apepat"));
                empleado.setCempApemat(rs.getString("cemp_apemat"));
                empleado.setCempCoDepend(rs.getInt("cemp_co_depend"));
                empleado.setCempIndbaj(rs.getInt("cemp_indbaj"));
                empleado.setDependencia(rs.getString("de_dependencia"));
                empleados.add(empleado);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al listar empleados: " + e.getMessage());
        }
        
        return empleados;
    }
}