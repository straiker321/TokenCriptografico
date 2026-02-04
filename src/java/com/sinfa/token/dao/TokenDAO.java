package com.sinfa.token.dao;

import com.sinfa.token.config.DatabaseConfig;
import com.sinfa.token.model.Token;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TokenDAO {
    
    /**
     * Listar TODOS los tokens (ADMIN)
     */
    public List<Token> listarTodosAdmin() {
        List<Token> tokens = new ArrayList<>();
        String sql = "SELECT at.idasignatoken, at.fecaccion, e.cemp_nu_dni, at.codemptok, " +
                    "e.cemp_apepat || ' ' || e.cemp_apemat || ' ' || e.cemp_denom as nombre_completo, " +
                    "e.cemp_indbaj, at.uniemptok, d.de_dependencia, " +
                    "at.flgtokcon, at.esttokcon, d2.de_dependencia as dep_entrega, " +
                    "at.tipaccion, at.codempcon, at.codempcon2, at.numdnitok, " +
                    "at.doc_sustento, at.doc_sustento_entrega, at.doc_sustento_final " +
                    "FROM t_m_asigna_token at " +
                    "INNER JOIN rhtm_per_empleados e ON at.codemptok = e.cemp_co_emp " +
                    "INNER JOIN rhtm_dependencia d ON at.uniemptok = d.co_dependencia " +
                    "LEFT JOIN rhtm_dependencia d2 ON at.unienttokcon = d2.co_dependencia " +
                    "WHERE at.estado = 1 " +
                    "ORDER BY " +
                    "CASE WHEN at.codempcon IS NULL THEN 0 " +
                    "     WHEN at.codempcon2 IS NULL THEN 1 " +
                    "     ELSE 2 END, " +
                    "at.fecaccion DESC, at.idasignatoken DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                tokens.add(mapearTokenBasico(rs));
            }
            
            System.out.println("✓ ADMIN GLOBAL: " + tokens.size() + " tokens (TODO)");
            
        } catch (SQLException e) {
            System.err.println("✗ Error admin global: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tokens;
    }
    
    /**
     * Listar tokens por UNIDAD (Implementador)
     */
    public List<Token> listarPorUnidad(int codigoEmpleado) {
        List<Token> tokens = new ArrayList<>();
        
        // Query según documento: ver tokens de su unidad O donde fue entregado
        String sql = "SELECT at.idasignatoken, at.fecaccion, e.cemp_nu_dni, at.codemptok, " +
                    "e.cemp_apepat || ' ' || e.cemp_apemat || ' ' || e.cemp_denom as nombre_completo, " +
                    "e.cemp_indbaj, at.uniemptok, d.de_dependencia, " +
                    "at.flgtokcon, at.esttokcon, d2.de_dependencia as dep_entrega, " +
                    "at.tipaccion, at.codempcon, at.codempcon2, at.numdnitok, " +
                    "at.doc_sustento, at.doc_sustento_entrega, at.doc_sustento_final " +
                    "FROM t_m_asigna_token at " +
                    "INNER JOIN rhtm_per_empleados e ON at.codemptok = e.cemp_co_emp " +
                    "INNER JOIN rhtm_dependencia d ON at.uniemptok = d.co_dependencia " +
                    "LEFT JOIN rhtm_dependencia d2 ON at.unienttokcon = d2.co_dependencia " +
                    "WHERE at.estado = 1 AND " +
                    "(substr(d.de_dependencia, 1, strpos(d.de_dependencia, ' ') -1) = " +
                    "(SELECT substr(dep.de_dependencia, 1, strpos(dep.de_dependencia, ' ') -1) " +
                    "FROM rhtm_dependencia dep " +
                    "INNER JOIN rhtm_per_empleados emp ON dep.co_dependencia = emp.cemp_co_depend " +
                    "WHERE emp.cemp_co_emp = ?)) " +
                    "UNION " +
                    "SELECT at.idasignatoken, at.fecaccion, e.cemp_nu_dni, at.codemptok, " +
                    "e.cemp_apepat || ' ' || e.cemp_apemat || ' ' || e.cemp_denom as nombre_completo, " +
                    "e.cemp_indbaj, at.uniemptok, d.de_dependencia, " +
                    "at.flgtokcon, at.esttokcon, d2.de_dependencia as dep_entrega, " +
                    "at.tipaccion, at.codempcon, at.codempcon2, at.numdnitok, " +
                    "at.doc_sustento, at.doc_sustento_entrega, at.doc_sustento_final " +
                    "FROM t_m_asigna_token at " +
                    "INNER JOIN rhtm_per_empleados e ON at.codemptok = e.cemp_co_emp " +
                    "INNER JOIN rhtm_dependencia d ON at.uniemptok = d.co_dependencia " +
                    "LEFT JOIN rhtm_dependencia d2 ON at.unienttokcon = d2.co_dependencia " +
                    "WHERE at.estado = 1 AND " +
                    "(substr(d2.de_dependencia, 1, strpos(d2.de_dependencia, ' ') -1) = " +
                    "(SELECT substr(dep.de_dependencia, 1, strpos(dep.de_dependencia, ' ') -1) " +
                    "FROM rhtm_dependencia dep " +
                    "INNER JOIN rhtm_per_empleados emp ON dep.co_dependencia = emp.cemp_co_depend " +
                    "WHERE emp.cemp_co_emp = ?)) " +
                    "ORDER BY fecaccion DESC, idasignatoken DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, codigoEmpleado);
            ps.setInt(2, codigoEmpleado);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Token token = mapearTokenBasico(rs);
                    tokens.add(token);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al listar tokens por unidad: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tokens;
    }
    
        public List<Token> listarParaImplementadorAdmin() {
    List<Token> tokens = new ArrayList<>();

    String sql =
        "SELECT at.idasignatoken, at.fecaccion, e.cemp_nu_dni, at.codemptok, " +
        "e.cemp_apepat || ' ' || e.cemp_apemat || ' ' || e.cemp_denom AS nombre_completo, " +
        "e.cemp_indbaj, at.uniemptok, d.de_dependencia, " +
        "at.flgtokcon, at.esttokcon, d2.de_dependencia AS dep_entrega, " +
        "at.tipaccion, at.codempcon, at.codempcon2, at.numdnitok, " +
        "at.doc_sustento, at.doc_sustento_entrega, at.doc_sustento_final " +
        "FROM t_m_asigna_token at " +
        "INNER JOIN rhtm_per_empleados e ON at.codemptok = e.cemp_co_emp " +
        "INNER JOIN rhtm_dependencia d ON at.uniemptok = d.co_dependencia " +
        "LEFT JOIN rhtm_dependencia d2 ON at.unienttokcon = d2.co_dependencia " +
        "WHERE at.estado = 1 " +
        "AND at.esttokcon IS NULL " +   // ✅ CONF 1 REAL
        "ORDER BY at.fecaccion DESC, at.idasignatoken DESC";

    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            tokens.add(mapearTokenBasico(rs));
        }

        System.out.println("✓ Impl Admin (CONF 1): " + tokens.size());

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return tokens;
}



    public List<Token> listarParaImplementadorNoAdmin() {
    List<Token> tokens = new ArrayList<>();

    String sql =
        "SELECT at.idasignatoken, at.fecaccion, e.cemp_nu_dni, at.codemptok, " +
        "e.cemp_apepat || ' ' || e.cemp_apemat || ' ' || e.cemp_denom AS nombre_completo, " +
        "e.cemp_indbaj, at.uniemptok, d.de_dependencia, " +
        "at.flgtokcon, at.esttokcon, d2.de_dependencia AS dep_entrega, " +
        "at.tipaccion, at.codempcon, at.codempcon2, at.numdnitok, " +
        "at.doc_sustento, at.doc_sustento_entrega, at.doc_sustento_final " +
        "FROM t_m_asigna_token at " +
        "INNER JOIN rhtm_per_empleados e ON at.codemptok = e.cemp_co_emp " +
        "INNER JOIN rhtm_dependencia d ON at.uniemptok = d.co_dependencia " +
        "LEFT JOIN rhtm_dependencia d2 ON at.unienttokcon = d2.co_dependencia " +
        "WHERE at.estado = 1 " +
        "AND at.esttokcon IN (1, 2) " +   // ✅ CONF 2 + COMPLETOS
        "ORDER BY " +
        "CASE WHEN at.esttokcon = 1 THEN 0 ELSE 1 END, " +
        "at.fecaccion DESC";

    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            tokens.add(mapearTokenBasico(rs));
        }

        System.out.println("✓ Impl No Admin (CONF 2 + COMPLETOS): " + tokens.size());

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return tokens;
}
  
    /**
     * Insertar nuevo token
     */
    public int insertar(Token token) {
        String sql = "INSERT INTO t_m_asigna_token " +
                    "(codempreg, uniregistra, numdnitok, codemptok, uniemptok, " +
                    "tipaccion, esttoken, fecaccion, codemprec, dniemprec, " +
                    "doc_sustento, estado, usucre, fecre) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                    "RETURNING idasignatoken";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, token.getCodempreg());
            ps.setInt(2, token.getUniregistra());
            ps.setString(3, token.getNumdnitok());
            ps.setInt(4, token.getCodemptok());
            ps.setInt(5, token.getUniemptok());
            ps.setInt(6, token.getTipaccion());
            ps.setInt(7, 1); // OPERATIVO por defecto
            ps.setDate(8, token.getFecaccion());
            
            if (token.getCodemprec() > 0) {
                ps.setInt(9, token.getCodemprec());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }
            
            ps.setString(10, token.getDniemprec());
            ps.setString(11, token.getDocSustento());
            ps.setInt(12, 1); // estado activo
            ps.setInt(13, token.getUsucre());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("✓ Token insertado con ID: " + id);
                    return id;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al insertar token: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    
    public boolean actualizarAdmin(Token token) {
        String sql = "UPDATE t_m_asigna_token SET " +
                    "uniregistra = ?, numdnitok = ?, codemptok = ?, " +
                    "uniemptok = ?, tipaccion = ?, fecaccion = ?, " +
                    "codemprec = ?, dniemprec = ?, doc_sustento = ?, " +
                    "usumod = ?, fecmod = CURRENT_TIMESTAMP " +
                    "WHERE idasignatoken = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, token.getUniregistra());
            ps.setString(2, token.getNumdnitok());
            ps.setInt(3, token.getCodemptok());
            ps.setInt(4, token.getUniemptok());
            ps.setInt(5, token.getTipaccion());
            ps.setDate(6, token.getFecaccion());
            
            if (token.getCodemprec() > 0) {
                ps.setInt(7, token.getCodemprec());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            
            ps.setString(8, token.getDniemprec());
            ps.setString(9, token.getDocSustento());
            ps.setInt(10, token.getUsumod());
            ps.setInt(11, token.getIdasignatoken());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Token actualizado (ADMIN): " + token.getIdasignatoken());
                return true;
            } else {
                System.out.println("⚠ No se actualizó ningún registro. ID: " + token.getIdasignatoken());
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar token (ADMIN): " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
   
    public boolean actualizarConfirmacion1(Token token) {
        String sql = "UPDATE t_m_asigna_token SET " +
                    "codempcon = ?, uniconfirma = ?, codemptokcon = ?, " +
                    "numdnitokcon = ?, uniemptokcon = ?, flgtokcon = ?, " +
                    "esttokcon = ?, unienttokcon = ?, fecentcon = ?, " +
                    "txtobscon = ?, doc_sustento_entrega = ?, " +
                    "usumod = ?, fecmod = CURRENT_TIMESTAMP " +
                    "WHERE idasignatoken = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, token.getCodempcon());
            ps.setInt(2, token.getUniconfirma());
            ps.setInt(3, token.getCodemptokcon());
            ps.setString(4, token.getNumdnitokcon());
            ps.setInt(5, token.getUniemptokcon());
            ps.setInt(6, token.getFlgtokcon());
            ps.setInt(7, token.getEsttokcon());
            
            if (token.getUnienttokcon() != null) {
                ps.setInt(8, token.getUnienttokcon());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            
            ps.setDate(9, token.getFecentcon());
            ps.setString(10, token.getTxtobscon());
            ps.setString(11, token.getDocSustentoEntrega());
            ps.setInt(12, token.getUsumod());
            ps.setInt(13, token.getIdasignatoken());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Primera confirmación actualizada para token: " + token.getIdasignatoken());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar confirmación 1: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
   
    public boolean actualizarConfirmacion2(Token token) {
        String sql = "UPDATE t_m_asigna_token SET " +
                     "codempcon2 = ?, uniconfirma2 = ?, numdnitokcon2 = ?, " +
                     "codemptokcon2 = ?, uniemptokcon2 = ?, flgtokcon2 = ?, " +
                     "esttokcon2 = ?, esttokcon = 2, " +
                     "unienttokcon2 = ?, fecentcon2 = ?, " +
                     "txtobscon2 = ?, doc_sustento_final = ?, " +
                     "usumod = ?, fecmod = CURRENT_TIMESTAMP " +
                     "WHERE idasignatoken = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, token.getCodempcon2());
            ps.setInt(2, token.getUniconfirma2());
            ps.setString(3, token.getNumdnitokcon2());
            ps.setInt(4, token.getCodemptokcon2());
            ps.setInt(5, token.getUniemptokcon2());
            ps.setInt(6, token.getFlgtokcon2());
            ps.setInt(7, token.getEsttokcon2());
            
            if (token.getUnienttokcon2() != null) {
                ps.setInt(8, token.getUnienttokcon2());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            
            ps.setDate(9, token.getFecentcon2());
            ps.setString(10, token.getTxtobscon2());
            ps.setString(11, token.getDocSustentoFinal());
            ps.setInt(12, token.getUsumod());
            ps.setInt(13, token.getIdasignatoken());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Segunda confirmación actualizada para token: " + token.getIdasignatoken());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar confirmación 2: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
   
    public Token obtenerPorId(int id) {
        Token token = null;
        String sql = "SELECT at.*, " +
                    "e.cemp_apepat || ' ' || e.cemp_apemat || ' ' || e.cemp_denom as nombre_completo, " +
                    "d.de_dependencia, " +
                    "e2.cemp_apepat || ' ' || e2.cemp_apemat || ' ' || e2.cemp_denom as nombre_confirma1, " +
                    "e3.cemp_apepat || ' ' || e3.cemp_apemat || ' ' || e3.cemp_denom as nombre_confirma2 " +
                    "FROM t_m_asigna_token at " +
                    "INNER JOIN rhtm_per_empleados e ON at.codemptok = e.cemp_co_emp " +
                    "INNER JOIN rhtm_dependencia d ON at.uniemptok = d.co_dependencia " +
                    "LEFT JOIN rhtm_per_empleados e2 ON at.codemptokcon = e2.cemp_co_emp " +
                    "LEFT JOIN rhtm_per_empleados e3 ON at.codemptokcon2 = e3.cemp_co_emp " +
                    "WHERE at.idasignatoken = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    token = mapearTokenCompleto(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener token: " + e.getMessage());
            e.printStackTrace();
        }
        
        return token;
    }
    
    /**
     * Eliminar (lógico)
     */
    public boolean eliminar(int id) {
        String sql = "UPDATE t_m_asigna_token SET estado = 0, fecmod = CURRENT_TIMESTAMP WHERE idasignatoken = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Token eliminado (lógico): " + id);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al eliminar token: " + e.getMessage());
        }
        
        return false;
    }
    
   
    public List<Token> buscar(String dni, String fechaDesde, String fechaHasta, boolean isAdmin, int codigoEmpleado) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT at.idasignatoken, at.fecaccion, e.cemp_nu_dni, at.codemptok, ");
        sql.append("e.cemp_apepat || ' ' || e.cemp_apemat || ' ' || e.cemp_denom as nombre_completo, ");
        sql.append("e.cemp_indbaj, at.uniemptok, d.de_dependencia, ");
        sql.append("at.flgtokcon, at.esttokcon, d2.de_dependencia as dep_entrega, ");
        sql.append("at.tipaccion, at.codempcon, at.codempcon2, at.numdnitok, ");
        sql.append("at.doc_sustento, at.doc_sustento_entrega, at.doc_sustento_final ");
        sql.append("FROM t_m_asigna_token at ");
        sql.append("INNER JOIN rhtm_per_empleados e ON at.codemptok = e.cemp_co_emp ");
        sql.append("INNER JOIN rhtm_dependencia d ON at.uniemptok = d.co_dependencia ");
        sql.append("LEFT JOIN rhtm_dependencia d2 ON at.unienttokcon = d2.co_dependencia ");
        sql.append("WHERE at.estado = 1 ");
        
        if (dni != null && !dni.trim().isEmpty()) {
            sql.append("AND at.numdnitok LIKE ? ");
        }
        
        if (fechaDesde != null && !fechaDesde.trim().isEmpty()) {
            sql.append("AND at.fecaccion >= ? ");
        }
        
        if (fechaHasta != null && !fechaHasta.trim().isEmpty()) {
            sql.append("AND at.fecaccion <= ? ");
        }
        
        sql.append("ORDER BY at.fecaccion DESC");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (dni != null && !dni.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + dni + "%");
            }
            
            if (fechaDesde != null && !fechaDesde.trim().isEmpty()) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(fechaDesde));
            }
            
            if (fechaHasta != null && !fechaHasta.trim().isEmpty()) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(fechaHasta));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tokens.add(mapearTokenBasico(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error en búsqueda: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tokens;
    }
    

    public int contarTotales() {
        return contar("SELECT COUNT(*) FROM t_m_asigna_token WHERE estado = 1");
    }
    
    public int contarOperativos() {
    return contar("SELECT COUNT(*) FROM t_m_asigna_token WHERE estado = 1");
    }

    
   public int contarPendientes() {
    return contar(
        "SELECT COUNT(*) FROM t_m_asigna_token " +
        "WHERE estado = 1 AND (esttokcon IS NULL OR esttokcon = 1)"
    );
    }

    public int contarConProblemas() {
        return contar("SELECT COUNT(*) FROM t_m_asigna_token WHERE estado = 1 AND esttokcon = 3");
    }
    
    public int contarCompletos() {
        return contar("SELECT COUNT(*) FROM t_m_asigna_token WHERE estado = 1 AND esttokcon = 2");
    }


    
    private int contar(String sql) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al contar: " + e.getMessage());
        }
        return 0;
    }
    

    private Token mapearTokenBasico(ResultSet rs) throws SQLException {
        Token token = new Token();
        token.setIdasignatoken(rs.getInt("idasignatoken"));
        token.setFecaccion(rs.getDate("fecaccion"));
        token.setNumdnitok(rs.getString("numdnitok"));
        token.setCodemptok(rs.getInt("codemptok"));
        token.setNombreUsuarioAsignado(rs.getString("nombre_completo"));
        token.setNombreDependencia(rs.getString("de_dependencia"));
        token.setTipaccion(rs.getInt("tipaccion"));
        
        // Documentos
        token.setDocSustento(rs.getString("doc_sustento"));
        token.setDocSustentoEntrega(rs.getString("doc_sustento_entrega"));
        token.setDocSustentoFinal(rs.getString("doc_sustento_final"));
        
        // Confirmaciones (pueden ser null)
        try {
            Integer codempcon = (Integer) rs.getObject("codempcon");
            if (codempcon != null) token.setCodempcon(codempcon);
            
            Integer codempcon2 = (Integer) rs.getObject("codempcon2");
            if (codempcon2 != null) token.setCodempcon2(codempcon2);
            
            Integer flgtokcon = (Integer) rs.getObject("flgtokcon");
            if (flgtokcon != null) token.setFlgtokcon(flgtokcon);
            
            Integer esttokcon = (Integer) rs.getObject("esttokcon");
            if (esttokcon != null) token.setEsttokcon(esttokcon);
        } catch (SQLException e) {
            // Campos opcionales
        }
        
        return token;
    }
    
 
    private Token mapearTokenCompleto(ResultSet rs) throws SQLException {
        Token token = new Token();
        
        // Datos principales
        token.setIdasignatoken(rs.getInt("idasignatoken"));
        token.setCodempreg(rs.getInt("codempreg"));
        token.setUniregistra(rs.getInt("uniregistra"));
        token.setNumdnitok(rs.getString("numdnitok"));
        token.setCodemptok(rs.getInt("codemptok"));
        token.setUniemptok(rs.getInt("uniemptok"));
        token.setTipaccion(rs.getInt("tipaccion"));
        token.setEsttoken(rs.getInt("esttoken"));
        token.setFecaccion(rs.getDate("fecaccion"));
        token.setNombreUsuarioAsignado(rs.getString("nombre_completo"));
        token.setNombreDependencia(rs.getString("de_dependencia"));
        token.setDocSustento(rs.getString("doc_sustento"));
        token.setEstado(rs.getInt("estado"));
        
        // Usuario que recibe (opcional)
        try {
            Integer codemprec = (Integer) rs.getObject("codemprec");
            if (codemprec != null) token.setCodemprec(codemprec);
            token.setDniemprec(rs.getString("dniemprec"));
        } catch (SQLException e) {}
        
        // Primera confirmación
        try {
            Integer codempcon = (Integer) rs.getObject("codempcon");
            if (codempcon != null) {
                token.setCodempcon(codempcon);
                token.setUniconfirma(rs.getInt("uniconfirma"));
                token.setCodemptokcon(rs.getInt("codemptokcon"));
                token.setNumdnitokcon(rs.getString("numdnitokcon"));
                token.setUniemptokcon(rs.getInt("uniemptokcon"));
                token.setFlgtokcon(rs.getInt("flgtokcon"));
                token.setEsttokcon(rs.getInt("esttokcon"));
                token.setFecentcon(rs.getDate("fecentcon"));
                token.setTxtobscon(rs.getString("txtobscon"));
                token.setDocSustentoEntrega(rs.getString("doc_sustento_entrega"));
                
                Integer unienttokcon = (Integer) rs.getObject("unienttokcon");
                if (unienttokcon != null) token.setUnienttokcon(unienttokcon);
            }
        } catch (SQLException e) {}
        
        // Segunda confirmación
        try {
            Integer codempcon2 = (Integer) rs.getObject("codempcon2");
            if (codempcon2 != null) {
                token.setCodempcon2(codempcon2);
                token.setUniconfirma2(rs.getInt("uniconfirma2"));
                token.setNumdnitokcon2(rs.getString("numdnitokcon2"));
                token.setCodemptokcon2(rs.getInt("codemptokcon2"));
                token.setUniemptokcon2(rs.getInt("uniemptokcon2"));
                token.setFlgtokcon2(rs.getInt("flgtokcon2"));
                token.setEsttokcon2(rs.getInt("esttokcon2"));
                token.setFecentcon2(rs.getDate("fecentcon2"));
                token.setTxtobscon2(rs.getString("txtobscon2"));
                token.setDocSustentoFinal(rs.getString("doc_sustento_final"));
                
                Integer unienttokcon2 = (Integer) rs.getObject("unienttokcon2");
                if (unienttokcon2 != null) token.setUnienttokcon2(unienttokcon2);
            }
        } catch (SQLException e) {}
        
        return token;
    }

}