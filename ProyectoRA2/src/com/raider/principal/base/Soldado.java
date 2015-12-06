package com.raider.principal.base;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by raider on 5/11/15.
 */
public class Soldado implements Serializable {

    private static final long serialVersionUID = 112325123L;

    private String nombre;
    private String apellidos;
    private Date fechaNacimiento;
    private String rango;
    private String lugarNacimiento;
    private String Unidad;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public String getLugarNacimiento() {
        return lugarNacimiento;
    }

    public void setLugarNacimiento(String lugarNacimiento) {
        this.lugarNacimiento = lugarNacimiento;
    }

    public String getUnidad() {
        return Unidad;
    }

    public void setUnidad(String unidad) {
        Unidad = unidad;
    }

    @Override
    public String toString() {
        return rango + " " + apellidos;
    }
}
