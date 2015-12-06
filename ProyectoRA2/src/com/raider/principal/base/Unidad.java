package com.raider.principal.base;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by raider on 5/11/15.
 */
public class Unidad implements Serializable {

    private static final long serialVersionUID = 112345123L;

    private String nUnidad;
    private String tipo;
    private String cuartel;
    private int noTropas;
    private Date fechaCreacion;

    public String getnUnidad() {
        return nUnidad;
    }

    public void setnUnidad(String nUnidad) {
        this.nUnidad = nUnidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCuartel() {
        return cuartel;
    }

    public void setCuartel(String cuartel) {
        this.cuartel = cuartel;
    }

    public int getNoTropas() {
        return noTropas;
    }

    public void setNoTropas(int noTropas) {
        this.noTropas = noTropas;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return tipo + " " + nUnidad;
    }
}
