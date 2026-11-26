package com.tejogestion.modulo3.modelo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un torneo registrado en TejoGestion.
 */
public class Torneo {
    private Integer idTorneo;
    private Integer refUsuario;
    private String nombre;
    private Date fechaInicio;
    private Date fechaFin;
    private String modalidad;
    private String reglamento;
    private Integer cupoMaximo;
    private Integer cuposDisponibles;
    private String estado;
    private List<Integer> canchasIds = new ArrayList<>();

    public Torneo() {
        this.estado = "InscripcionesAbiertas";
    }

    public Torneo(Integer refUsuario, String nombre, Date fechaInicio, Date fechaFin,
                  String modalidad, String reglamento, Integer cupoMaximo) {
        this.refUsuario = refUsuario;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.modalidad = modalidad;
        this.reglamento = reglamento;
        this.cupoMaximo = cupoMaximo;
        this.cuposDisponibles = cupoMaximo;
        this.estado = "InscripcionesAbiertas";
    }

    public boolean hayCuposDisponibles() {
        return cuposDisponibles != null && cuposDisponibles > 0;
    }

    public void descontarCupo() {
        if (!hayCuposDisponibles()) {
            throw new IllegalStateException("No hay cupos disponibles en el torneo.");
        }
        this.cuposDisponibles--;
    }

    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public Integer getIdTorneo() { return idTorneo; }
    public void setIdTorneo(Integer idTorneo) { this.idTorneo = idTorneo; }
    public Integer getRefUsuario() { return refUsuario; }
    public void setRefUsuario(Integer refUsuario) { this.refUsuario = refUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    public String getReglamento() { return reglamento; }
    public void setReglamento(String reglamento) { this.reglamento = reglamento; }
    public Integer getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(Integer cupoMaximo) { this.cupoMaximo = cupoMaximo; }
    public Integer getCuposDisponibles() { return cuposDisponibles; }
    public void setCuposDisponibles(Integer cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<Integer> getCanchasIds() { return canchasIds; }
    public void setCanchasIds(List<Integer> canchasIds) { this.canchasIds = canchasIds != null ? canchasIds : new ArrayList<>(); }
}
