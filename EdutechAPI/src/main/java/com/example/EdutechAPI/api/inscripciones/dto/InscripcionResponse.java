// src/main/java/com/example/EdutechAPI/api/inscripciones/dto/InscripcionResponse.java
package com.example.EdutechAPI.api.inscripciones.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionResponse {

    private Long idInscripcion;
    private Long idUsuario;     // <-- Este es el ID del usuario que quieres
    private Long idCurso;       // <-- Este es el ID del curso que quieres
    // Opcional: Si quieres ver el nombre del usuario o curso en la respuesta, puedes añadirlos
    private String nombreUsuario;
    private String nombreCurso;

    private Date fechaInscripcion;
    private String estado;

    // Constructor para mapear desde la entidad Inscripcion
    public InscripcionResponse(Long idInscripcion, Long idUsuario, String nombreUsuario, Long idCurso, String nombreCurso, Date fechaInscripcion, String estado) {
        this.idInscripcion = idInscripcion;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario; // Si no lo quieres, puedes omitirlo
        this.idCurso = idCurso;
        this.nombreCurso = nombreCurso;     // Si no lo quieres, puedes omitirlo
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
    }
}