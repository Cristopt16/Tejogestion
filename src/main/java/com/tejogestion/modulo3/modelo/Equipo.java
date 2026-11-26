package com.tejogestion.modulo3.modelo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un equipo inscrito en un torneo.
 */
public class Equipo {
    private Integer idEquipo;
    private Integer refTorneo;
    private String nombreEquipo;
    private Timestamp fechaInscripcion;
    private String estado;
    private List<Participante> participantes = new ArrayList<>();

    public Equipo() {
        this.estado = "Inscrito";
    }

    public Equipo(Integer refTorneo, String nombreEquipo) {
        this.refTorneo = refTorneo;
        this.nombreEquipo = nombreEquipo;
        this.estado = "Inscrito";
    }

    public void agregarParticipante(Participante participante) {
        if (participante != null) {
            this.participantes.add(participante);
        }
    }

    public Integer getIdEquipo() { return idEquipo; }
    public void setIdEquipo(Integer idEquipo) { this.idEquipo = idEquipo; }
    public Integer getRefTorneo() { return refTorneo; }
    public void setRefTorneo(Integer refTorneo) { this.refTorneo = refTorneo; }
    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }
    public Timestamp getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(Timestamp fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<Participante> getParticipantes() { return participantes; }
    public void setParticipantes(List<Participante> participantes) {
        this.participantes = participantes != null ? participantes : new ArrayList<>();
    }
}
