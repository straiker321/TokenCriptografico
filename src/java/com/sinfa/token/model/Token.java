package com.sinfa.token.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Token {
    private int idasignatoken;
    private int codempreg;
    private int uniregistra;
    private String numdnitok;
    private int codemptok;
    private int uniemptok;
    private int tipaccion;
    private int esttoken;
    private Date fecaccion;
    private int codemprec;
    private String dniemprec;
    private String docSustento;
    
    // Primera confirmación
    private Integer codempcon;
    private Integer uniconfirma;
    private Integer codemptokcon;
    private String numdnitokcon;
    private Integer uniemptokcon;
    private Integer flgtokcon;
    private Integer esttokcon;
    private Integer unienttokcon;
    private Date fecentcon;
    private String txtobscon;
    private String docSustentoEntrega;
    
    // Segunda confirmación
    private Integer codempcon2;
    private Integer uniconfirma2;
    private String numdnitokcon2;
    private Integer codemptokcon2;
    private Integer uniemptokcon2;
    private Integer flgtokcon2;
    private Integer esttokcon2;
    private Integer unienttokcon2;
    private Date fecentcon2;
    private String txtobscon2;
    private String docSustentoFinal;
    
    // Auditoría
    private int estado;
    private int usucre;
    private Timestamp fecre;
    private Integer usumod;
    private Timestamp fecmod;
    
    // Datos adicionales para visualización
    private String nombreUsuarioAsignado;
    private String nombreDependencia;
    private String nombreUsuarioRecibe;
    
    public Token() {}
    
    // Getters y Setters principales
    public int getIdasignatoken() { return idasignatoken; }
    public void setIdasignatoken(int idasignatoken) { this.idasignatoken = idasignatoken; }
    
    public int getCodempreg() { return codempreg; }
    public void setCodempreg(int codempreg) { this.codempreg = codempreg; }
    
    public int getUniregistra() { return uniregistra; }
    public void setUniregistra(int uniregistra) { this.uniregistra = uniregistra; }
    
    public String getNumdnitok() { return numdnitok; }
    public void setNumdnitok(String numdnitok) { this.numdnitok = numdnitok; }
    
    public int getCodemptok() { return codemptok; }
    public void setCodemptok(int codemptok) { this.codemptok = codemptok; }
    
    public int getUniemptok() { return uniemptok; }
    public void setUniemptok(int uniemptok) { this.uniemptok = uniemptok; }
    
    public int getTipaccion() { return tipaccion; }
    public void setTipaccion(int tipaccion) { this.tipaccion = tipaccion; }
    
    public int getEsttoken() { return esttoken; }
    public void setEsttoken(int esttoken) { this.esttoken = esttoken; }
    
    public Date getFecaccion() { return fecaccion; }
    public void setFecaccion(Date fecaccion) { this.fecaccion = fecaccion; }
    
    public int getCodemprec() { return codemprec; }
    public void setCodemprec(int codemprec) { this.codemprec = codemprec; }
    
    public String getDniemprec() { return dniemprec; }
    public void setDniemprec(String dniemprec) { this.dniemprec = dniemprec; }
    
    public String getDocSustento() { return docSustento; }
    public void setDocSustento(String docSustento) { this.docSustento = docSustento; }
    
    // Getters y Setters confirmación 1
    public Integer getCodempcon() { return codempcon; }
    public void setCodempcon(Integer codempcon) { this.codempcon = codempcon; }
    
    public Integer getUniconfirma() { return uniconfirma; }
    public void setUniconfirma(Integer uniconfirma) { this.uniconfirma = uniconfirma; }
    
    public Integer getCodemptokcon() { return codemptokcon; }
    public void setCodemptokcon(Integer codemptokcon) { this.codemptokcon = codemptokcon; }
    
    public String getNumdnitokcon() { return numdnitokcon; }
    public void setNumdnitokcon(String numdnitokcon) { this.numdnitokcon = numdnitokcon; }
    
    public Integer getUniemptokcon() { return uniemptokcon; }
    public void setUniemptokcon(Integer uniemptokcon) { this.uniemptokcon = uniemptokcon; }
    
    public Integer getFlgtokcon() { return flgtokcon; }
    public void setFlgtokcon(Integer flgtokcon) { this.flgtokcon = flgtokcon; }
    
    public Integer getEsttokcon() { return esttokcon; }
    public void setEsttokcon(Integer esttokcon) { this.esttokcon = esttokcon; }
    
    public Integer getUnienttokcon() { return unienttokcon; }
    public void setUnienttokcon(Integer unienttokcon) { this.unienttokcon = unienttokcon; }
    
    public Date getFecentcon() { return fecentcon; }
    public void setFecentcon(Date fecentcon) { this.fecentcon = fecentcon; }
    
    public String getTxtobscon() { return txtobscon; }
    public void setTxtobscon(String txtobscon) { this.txtobscon = txtobscon; }
    
    public String getDocSustentoEntrega() { return docSustentoEntrega; }
    public void setDocSustentoEntrega(String docSustentoEntrega) { this.docSustentoEntrega = docSustentoEntrega; }
    
    // Getters y Setters confirmación 2
    public Integer getCodempcon2() { return codempcon2; }
    public void setCodempcon2(Integer codempcon2) { this.codempcon2 = codempcon2; }
    
    public Integer getUniconfirma2() { return uniconfirma2; }
    public void setUniconfirma2(Integer uniconfirma2) { this.uniconfirma2 = uniconfirma2; }
    
    public String getNumdnitokcon2() { return numdnitokcon2; }
    public void setNumdnitokcon2(String numdnitokcon2) { this.numdnitokcon2 = numdnitokcon2; }
    
    public Integer getCodemptokcon2() { return codemptokcon2; }
    public void setCodemptokcon2(Integer codemptokcon2) { this.codemptokcon2 = codemptokcon2; }
    
    public Integer getUniemptokcon2() { return uniemptokcon2; }
    public void setUniemptokcon2(Integer uniemptokcon2) { this.uniemptokcon2 = uniemptokcon2; }
    
    public Integer getFlgtokcon2() { return flgtokcon2; }
    public void setFlgtokcon2(Integer flgtokcon2) { this.flgtokcon2 = flgtokcon2; }
    
    public Integer getEsttokcon2() { return esttokcon2; }
    public void setEsttokcon2(Integer esttokcon2) { this.esttokcon2 = esttokcon2; }
    
    public Integer getUnienttokcon2() { return unienttokcon2; }
    public void setUnienttokcon2(Integer unienttokcon2) { this.unienttokcon2 = unienttokcon2; }
    
    public Date getFecentcon2() { return fecentcon2; }
    public void setFecentcon2(Date fecentcon2) { this.fecentcon2 = fecentcon2; }
    
    public String getTxtobscon2() { return txtobscon2; }
    public void setTxtobscon2(String txtobscon2) { this.txtobscon2 = txtobscon2; }
    
    public String getDocSustentoFinal() { return docSustentoFinal; }
    public void setDocSustentoFinal(String docSustentoFinal) { this.docSustentoFinal = docSustentoFinal; }
    
    // Auditoría
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
    
    public int getUsucre() { return usucre; }
    public void setUsucre(int usucre) { this.usucre = usucre; }
    
    public Timestamp getFecre() { return fecre; }
    public void setFecre(Timestamp fecre) { this.fecre = fecre; }
    
    public Integer getUsumod() { return usumod; }
    public void setUsumod(Integer usumod) { this.usumod = usumod; }
    
    public Timestamp getFecmod() { return fecmod; }
    public void setFecmod(Timestamp fecmod) { this.fecmod = fecmod; }
    
    // Datos adicionales
    public String getNombreUsuarioAsignado() { return nombreUsuarioAsignado; }
    public void setNombreUsuarioAsignado(String nombreUsuarioAsignado) { 
        this.nombreUsuarioAsignado = nombreUsuarioAsignado; 
    }
    
    public String getNombreDependencia() { return nombreDependencia; }
    public void setNombreDependencia(String nombreDependencia) { 
        this.nombreDependencia = nombreDependencia; 
    }
    
    public String getNombreUsuarioRecibe() { return nombreUsuarioRecibe; }
    public void setNombreUsuarioRecibe(String nombreUsuarioRecibe) { 
        this.nombreUsuarioRecibe = nombreUsuarioRecibe; 
    }
    
    // Métodos auxiliares
    public String getTipAccionTexto() {
        return tipaccion == 1 ? "EMISIÓN" : "INTERNAMIENTO";
    }
    
    public String getEstTokenTexto() {
        switch (esttoken) {
            case 1: return "OPERATIVO";
            case 2: return "MALOGRADO";
            case 3: return "PERDIDO";
            case 4: return "ENTREGADO";
            default: return "N/A";
        }
    }
    
    public String getFlgTokConTexto() {
        if (flgtokcon == null) return "N/A";
        return flgtokcon == 1 ? "SIN TOKEN" : "CON TOKEN";
    }
    
    public boolean isPendienteConfirmacionInicial() {
        return codempcon == null && codempcon2 == null;
    }
    
    public boolean isPendienteConfirmacionFinal() {
        return codempcon != null && codempcon2 == null;
    }
    
    public boolean isCompleto() {
        return codempcon2 != null && codempcon2 == null;
    }
    
    @Override
    public String toString() {
        return "Token{" +
                "idasignatoken=" + idasignatoken +
                ", numdnitok='" + numdnitok + '\'' +
                ", nombreUsuarioAsignado='" + nombreUsuarioAsignado + '\'' +
                ", nombreDependencia='" + nombreDependencia + '\'' +
                ", tipaccion=" + getTipAccionTexto() +
                ", estado=" + estado +
                '}';
    }
}