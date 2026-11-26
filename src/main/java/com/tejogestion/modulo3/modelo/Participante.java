package com.tejogestion.modulo3.modelo;

/**
 * Representa un jugador perteneciente a un equipo de torneo.
 */
public class Participante {
    private Integer idParticipante;
    private Integer refEquipo;
    private String nombreCompleto;
    private String documentoTelefono;

    public Participante() {}

    public Participante(String nombreCompleto, String documentoTelefono) {
        this.nombreCompleto = nombreCompleto;
        this.documentoTelefono = documentoTelefono;
    }

    public Integer getIdParticipante() { return idParticipante; }
    public void setIdParticipante(Integer idParticipante) { this.idParticipante = idParticipante; }
    public Integer getRefEquipo() { return refEquipo; }
    public void setRefEquipo(Integer refEquipo) { this.refEquipo = refEquipo; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getDocumentoTelefono() { return documentoTelefono; }
    public void setDocumentoTelefono(String documentoTelefono) { this.documentoTelefono = documentoTelefono; }
}
