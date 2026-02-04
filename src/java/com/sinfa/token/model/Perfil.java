package com.sinfa.token.model;

import java.sql.Timestamp;

public class Perfil {
    private int idPerfil;
    private String nombrePerfil;
    private String descripcion;
    private Timestamp createdAt;
    
    public Perfil() {}
    
    public Perfil(int idPerfil, String nombrePerfil) {
        this.idPerfil = idPerfil;
        this.nombrePerfil = nombrePerfil;
    }
    
    // Getters y Setters
    public int getIdPerfil() { return idPerfil; }
    public void setIdPerfil(int idPerfil) { this.idPerfil = idPerfil; }
    
    public String getNombrePerfil() { return nombrePerfil; }
    public void setNombrePerfil(String nombrePerfil) { this.nombrePerfil = nombrePerfil; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Perfil{" +
                "idPerfil=" + idPerfil +
                ", nombrePerfil='" + nombrePerfil + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}