package com.example.EdutechAPI.api.usuarios.model;

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
@Table(name = "USUARIOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "NOMBRE", length = 50)
    private String nombre;

    @Column(name = "APELLIDO", length = 50)
    private String apellido;

    @Column(name = "EMAIL", length = 100, unique = true)
    private String email;

    @Column(name = "CONTRASENA", length = 255)
    private String contrasena;

    @Column(name = "FECHA_REGISTRO")
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;

    @Column(name = "ESTADO", length = 20)
    private String estado;

    @Column(name = "ID_OFICINA")
    private Long idOficina;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "USUARIO_ROL",
        joinColumns = @JoinColumn(name = "USUARIOS_ID_USUARIO"),
        inverseJoinColumns = @JoinColumn(name = "ROLES_ID_ROL")
    )
    private Set<Rol> roles = new HashSet<>();

    // RELACIÓN BIDIRECCIONAL CON INSCRIPCION
    // 'mappedBy' indica el nombre de la propiedad en la entidad Inscripcion que mapea esta relación.
    // FetchType.LAZY es crucial para no cargar todas las inscripciones del usuario a la vez.
    // @JsonIgnore previene ciclos infinitos de serialización al convertir a JSON.
    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inscripcion> inscripciones;
}