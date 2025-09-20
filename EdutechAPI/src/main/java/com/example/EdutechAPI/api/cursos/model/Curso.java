package com.example.EdutechAPI.api.cursos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore; // ¡Importa esta línea!
import com.example.EdutechAPI.api.inscripciones.model.Inscripcion; // ¡Importa esta línea si aún no la tienes!

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CURSOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "curso_seq_generator")
    @SequenceGenerator(name = "curso_seq_generator", sequenceName = "cursos_id_curso_seq", allocationSize = 1)
    @Column(name = "ID_CURSO")
    private Long idCurso;

    @Column(name = "NOMBRE_CURSO", length = 100)
    private String nombreCurso;

    @Column(name = "DESCRIPCION", length = 4000)
    private String descripcion;

    @Column(name = "FECHA_CREACION")
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @Column(name = "ESTADO", length = 20)
    private String estado;

    // RELACIÓN BIDIRECCIONAL CON INSCRIPCION
    // 'mappedBy' indica el nombre de la propiedad en la entidad Inscripcion que mapea esta relación.
    // FetchType.LAZY es crucial para no cargar todas las inscripciones del curso a la vez.
    // @JsonIgnore previene ciclos infinitos de serialización al convertir a JSON.
    @JsonIgnore
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inscripcion> inscripciones;
}