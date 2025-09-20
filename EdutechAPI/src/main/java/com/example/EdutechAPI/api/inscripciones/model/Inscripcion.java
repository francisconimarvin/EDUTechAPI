package com.example.EdutechAPI.api.inscripciones.model;

import com.example.EdutechAPI.api.cursos.model.Curso;
import com.example.EdutechAPI.api.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
// NOTA: No necesitamos aquí las anotaciones de Jackson si el módulo de Hibernate las maneja.
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

@Entity
@Table(name = "INSCRIPCIONES")
@Data
@NoArgsConstructor
@AllArgsConstructor
// Quita si tenías @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inscripcion_seq_generator")
    @SequenceGenerator(name = "inscripcion_seq_generator", sequenceName = "inscripciones_id_inscripcion_seq", allocationSize = 1)
    @Column(name = "ID_INSCRIPCION")
    private Long idInscripcion;

    @ManyToOne(fetch = FetchType.LAZY) // ¡Mantén LAZY!
    @JoinColumn(name = "USUARIOS_ID_USUARIO", nullable = false)
    // ¡Asegúrate de que no haya @JsonIgnore aquí!
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY) // ¡Mantén LAZY!
    @JoinColumn(name = "CURSOS_ID_CURSO", nullable = false)
    // ¡Asegúrate de que no haya @JsonIgnore aquí!
    private Curso curso;

    @Column(name = "FECHA_INSCRIPCION")
    @Temporal(TemporalType.DATE)
    private Date fechaInscripcion;

    @Column(name = "ESTADO")
    private String estado;
}