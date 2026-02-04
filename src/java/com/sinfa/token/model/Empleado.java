package com.sinfa.token.model;

import java.sql.Timestamp;

public class Empleado {
    private int cempCoEmp;
    private String cempNuDni;
    private String cempDenom;
    private String cempApepat;
    private String cempApemat;
    private int cempCoDepend;
    private int cempIndbaj;
    private Timestamp createdAt;
    
    // Datos relacionados
    private String nombreCompleto;
    private String dependencia;
    private String estadoTexto;
    
    public Empleado() {}
    
    public Empleado(String cempNuDni, String cempDenom, String cempApepat, String cempApemat) {
        this.cempNuDni = cempNuDni;
        this.cempDenom = cempDenom;
        this.cempApepat = cempApepat;
        this.cempApemat = cempApemat;
    }
    
    // Getters y Setters
    public int getCempCoEmp() { return cempCoEmp; }
    public void setCempCoEmp(int cempCoEmp) { this.cempCoEmp = cempCoEmp; }
    
    public String getCempNuDni() { return cempNuDni; }
    public void setCempNuDni(String cempNuDni) { this.cempNuDni = cempNuDni; }
    
    public String getCempDenom() { return cempDenom; }
    public void setCempDenom(String cempDenom) { this.cempDenom = cempDenom; }
    
    public String getCempApepat() { return cempApepat; }
    public void setCempApepat(String cempApepat) { this.cempApepat = cempApepat; }
    
    public String getCempApemat() { return cempApemat; }
    public void setCempApemat(String cempApemat) { this.cempApemat = cempApemat; }
    
    public int getCempCoDepend() { return cempCoDepend; }
    public void setCempCoDepend(int cempCoDepend) { this.cempCoDepend = cempCoDepend; }
    
    public int getCempIndbaj() { return cempIndbaj; }
    public void setCempIndbaj(int cempIndbaj) { this.cempIndbaj = cempIndbaj; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public String getNombreCompleto() {
        if (nombreCompleto != null) {
            return nombreCompleto;
        }
        return cempApepat + " " + cempApemat + " " + cempDenom;
    }
    
    public void setNombreCompleto(String nombreCompleto) { 
        this.nombreCompleto = nombreCompleto; 
    }
    
    public String getDependencia() { return dependencia; }
    public void setDependencia(String dependencia) { this.dependencia = dependencia; }
    
    public String getEstadoTexto() {
        if (estadoTexto != null) {
            return estadoTexto;
        }
        return cempIndbaj == 1 ? "ACTIVO" : "DE BAJA";
    }
    
    public void setEstadoTexto(String estadoTexto) { 
        this.estadoTexto = estadoTexto; 
    }
    
    public boolean isActivo() {
        return cempIndbaj == 1;
    }
    
    @Override
    public String toString() {
        return "Empleado{" +
                "cempCoEmp=" + cempCoEmp +
                ", cempNuDni='" + cempNuDni + '\'' +
                ", nombreCompleto='" + getNombreCompleto() + '\'' +
                ", dependencia='" + dependencia + '\'' +
                ", estado='" + getEstadoTexto() + '\'' +
                '}';
    }
}