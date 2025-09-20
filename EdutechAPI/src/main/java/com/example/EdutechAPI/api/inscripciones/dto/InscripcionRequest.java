package com.example.EdutechAPI.api.inscripciones.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionRequest {

    private Long idUsuario;
    private Long idCurso;

    
    private Date fechaInscripcion;
    private String estado;
}
    