package com.sinfa.token.model;

import java.sql.Timestamp;

public class Usuario {
    private int idUsuario;
    private String username;
    private String password;
    private int cempCoEmp;
    private int idPerfil;
    private int activo;
    private Timestamp ultimoAcceso;
    private Timestamp createdAt;
    
    // Datos relacionados
    private String nombreCompleto;
    private String nombrePerfil;
    private String dependencia;
    
    // Constructores
    public Usuario() {}
    
    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getCempCoEmp() { return cempCoEmp; }
    public void setCempCoEmp(int cempCoEmp) { this.cempCoEmp = cempCoEmp; }
    
    public int getIdPerfil() { return idPerfil; }
    public void setIdPerfil(int idPerfil) { this.idPerfil = idPerfil; }
    
    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
    
    public Timestamp getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(Timestamp ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public String getNombrePerfil() { return nombrePerfil; }
    public void setNombrePerfil(String nombrePerfil) { this.nombrePerfil = nombrePerfil; }
    
    public String getDependencia() { return dependencia; }
    public void setDependencia(String dependencia) { this.dependencia = dependencia; }
    
    public boolean isAdmin() {
        if(nombrePerfil != null && !nombrePerfil.trim().isEmpty()){
          return "ADMINISTRADOR".equalsIgnoreCase(nombrePerfil.trim());      
        }
        return idPerfil == 1;
    }
    
    public String getRolDisplay(){
        return isAdmin() ? "ADMINISTRADOR" : "IMPLEMENTADOR";
    }
    
    public boolean isActivo(){
        return activo == 1;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", username='" + username + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", nombrePerfil='" + nombrePerfil + '\'' +
                ", activo=" + activo +
                '}';
    }
}