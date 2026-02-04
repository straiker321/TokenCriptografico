package com.sinfa.token.model;

import java.sql.Timestamp;

public class Dependencia {
    private int coDependencia;
    private String deDependencia;
    private int coNivel;
    private int inBaja;
    private Timestamp createdAt;
    
    public Dependencia() {}
    
    public Dependencia(int coDependencia, String deDependencia) {
        this.coDependencia = coDependencia;
        this.deDependencia = deDependencia;
    }
    
    // Getters y Setters
    public int getCoDependencia() { return coDependencia; }
    public void setCoDependencia(int coDependencia) { this.coDependencia = coDependencia; }
    
    public String getDeDependencia() { return deDependencia; }
    public void setDeDependencia(String deDependencia) { this.deDependencia = deDependencia; }
    
    public int getCoNivel() { return coNivel; }
    public void setCoNivel(int coNivel) { this.coNivel = coNivel; }
    
    public int getInBaja() { return inBaja; }
    public void setInBaja(int inBaja) { this.inBaja = inBaja; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public boolean isActiva() {
        return inBaja == 0;
    }
    
    @Override
    public String toString() {
        return "Dependencia{" +
                "coDependencia=" + coDependencia +
                ", deDependencia='" + deDependencia + '\'' +
                ", coNivel=" + coNivel +
                ", activa=" + isActiva() +
                '}';
    }
}