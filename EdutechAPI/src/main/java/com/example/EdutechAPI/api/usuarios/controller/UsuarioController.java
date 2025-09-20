package com.example.EdutechAPI.api.usuarios.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EdutechAPI.api.usuarios.model.Rol;
import com.example.EdutechAPI.api.usuarios.model.Usuario;
import com.example.EdutechAPI.api.usuarios.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Usuarios", description = "Gestión completa de usuarios y sus roles dentro de la Edutech API.")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // --- Endpoints para CRUD de Usuarios ---

    @Operation(summary = "Obtener todos los usuarios",
               description = "Recupera una lista de todos los usuarios registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada exitosamente",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = Usuario.class)))
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @Operation(summary = "Obtener usuario por ID",
               description = "Recupera los detalles de un usuario específico utilizando su ID único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(
            @Parameter(description = "ID único del usuario a buscar", required = true, example = "1")
            @PathVariable("id") Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Obtener usuario por Email",
               description = "Recupera los detalles de un usuario específico utilizando su dirección de email.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                     content = @Content)
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> getUsuarioByEmail(
            @Parameter(description = "Dirección de correo electrónico del usuario a buscar", required = true, example = "jon.snow@example.com")
            @PathVariable("email") String email) {
        return usuarioService.obtenerUsuarioPorEmail(email)
                .map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Crear un nuevo usuario",
               description = "Registra un nuevo usuario en el sistema. Se recomienda proporcionar un 'idUsuario' único si no es autogenerado por la BD.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. email ya existente, ID no disponible)",
                     content = @Content(mediaType = "application/json",
                     examples = @ExampleObject(name = "Invalid Input", value = "{\"error\": \"El email 'jon.snow@example.com' ya está registrado.\"}" ))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content(mediaType = "application/json",
                     examples = @ExampleObject(name = "Internal Server Error", value = "{\"error\": \"Error interno del servidor al crear usuario: Mensaje del error.\"}" )))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del nuevo usuario a crear. 'idUsuario' debe ser único. 'email' debe ser único.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(name = "Example User Creation", value = """
                {
                    "idUsuario": 1,
                    "nombre": "Jon Snow",
                    "apellido": "Jon Snow",
                    "email": "jon.snow@example.com",
                    "contrasena": "hash_password_1",
                    "estado": "inactivo",
                    "idOficina": 105
                }
                """)
        )
    )
    @PostMapping
    // ¡CAMBIO AQUÍ! Cambia ResponseEntity<?> a ResponseEntity<Object>
    public ResponseEntity<Object> createUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Collections.singletonMap("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Error interno del servidor al crear usuario: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Actualizar un usuario existente",
               description = "Actualiza el nombre, apellido, email, estado e ID de oficina de un usuario por su ID. La contraseña no se puede modificar por este endpoint.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Email ya existe para otro usuario o datos inválidos",
                     content = @Content(mediaType = "application/json",
                     examples = @ExampleObject(name = "Duplicate Email", value = "{\"error\": \"El email 'aegon.targaryen@example.com' ya está registrado para otro usuario.\"}" )))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del usuario a actualizar. Solo se pueden modificar nombre, apellido, email, estado e idOficina.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(name = "Example User Update", value = """
                {
                    "nombre": "Aegon Targaryen",
                    "apellido": "Aegon Targaryen",
                    "email": "aegon.targaryen@example.com",
                    "estado": "activo",
                    "idOficina": 101
                }
                """)
        )
    )
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable("id") Long id, @RequestBody Usuario usuarioDetalles) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDetalles);
            return new ResponseEntity<>(usuarioActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Eliminar un usuario",
               description = "Elimina un usuario del sistema por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente (No Content)"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUsuario(
            @Parameter(description = "ID del usuario a eliminar", required = true, example = "2")
            @PathVariable("id") Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Endpoints para Gestión de Roles de Usuarios ---

    @Operation(summary = "Obtener los roles de un usuario",
               description = "Recupera la lista de roles asignados a un usuario específico por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles del usuario recuperados exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Rol.class, type = "array"))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                     content = @Content)
    })
    @GetMapping("/{id}/roles")
    public ResponseEntity<Set<Rol>> getRolesByUsuarioId(@PathVariable("id") Long id) {
        try {
            Set<Rol> roles = usuarioService.obtenerRolesDeUsuario(id);
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // POST /api/usuarios/{id}/roles
    @Operation(summary = "Asignar un rol a un usuario",
               description = "Asigna un rol específico a un usuario. Los roles válidos son: 'Estudiante', 'Profesor', 'Administrador', 'Lord Commander of the Night's Watch'. Los roles no pueden estar repetidos para un usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol asignado exitosamente (retorna los roles actualizados del usuario)",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Rol.class, type = "array", example = """
                         [
                             { "idRol": 1, "nombre": "Estudiante" },
                             { "idRol": 2, "nombre": "Profesor" }
                         ]
                         """))),
        @ApiResponse(responseCode = "400", description = "Rol inválido, ya asignado o 'nombreRol' es nulo/vacío",
                     content = @Content(mediaType = "application/json",
                     examples = @ExampleObject(name = "Invalid Role or Duplicate", value = "{\"error\": \"Rol 'Lord Commander of the Night's Watch' no existe o ya está asignado.\"}" ))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content(mediaType = "application/json",
                     examples = @ExampleObject(name = "Internal Server Error", value = "{\"error\": \"Error interno del servidor al asignar rol: Mensaje del error.\"}" )))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Objeto JSON con el nombre del rol a asignar.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(name = "Assign Role Example", value = """
                {
                    "nombreRol": "Estudiante"
                }
                """)
        )
    )
    @PostMapping("/{id}/roles")
    public ResponseEntity<Object> asignarRolAUsuario(@PathVariable("id") Long id, @RequestBody Map<String, String> requestBody) {
        String nombreRol = requestBody.get("nombreRol");
        if (nombreRol == null || nombreRol.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("error", "El campo 'nombreRol' es requerido."), HttpStatus.BAD_REQUEST);
        }
        try {
            Usuario usuarioActualizado = usuarioService.asignarRolAUsuario(id, nombreRol);
            return new ResponseEntity<>(usuarioActualizado.getRoles(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Collections.singletonMap("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Error interno del servidor al asignar rol: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE /api/usuarios/{id}/roles
    @Operation(summary = "Desasignar un rol de un usuario",
               description = "Elimina un rol específico de un usuario. El rol debe ser válido y estar asignado previamente al usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol desasignado exitosamente (retorna los roles actualizados del usuario)",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Rol.class, type = "array", example = """
                         [
                             { "idRol": 1, "nombre": "Estudiante" }
                         ]
                         """))),
        @ApiResponse(responseCode = "400", description = "Rol inválido o no asignado al usuario",
                     content = @Content(mediaType = "application/json",
                     examples = @ExampleObject(name = "Invalid or Missing Role", value = "{\"error\": \"Rol 'Estudiante' no está asignado al usuario o no existe.\"}" ))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                     content = @Content(mediaType = "application/json",
                     examples = @ExampleObject(name = "Internal Server Error", value = "{\"error\": \"Error interno del servidor al desasignar rol: Mensaje del error.\"}" )))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Objeto JSON con el nombre del rol a desasignar.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(name = "Remove Role Example", value = """
                {
                    "nombreRol": "Estudiante"
                }
                """)
        )
    )
    @DeleteMapping("/{id}/roles")
    public ResponseEntity<Object> desasignarRolAUsuario(@PathVariable("id") Long id, @RequestBody Map<String, String> requestBody) {
        String nombreRol = requestBody.get("nombreRol");
        if (nombreRol == null || nombreRol.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("error", "El campo 'nombreRol' es requerido."), HttpStatus.BAD_REQUEST);
        }
        try {
            Usuario usuarioActualizado = usuarioService.desasignarRolAUsuario(id, nombreRol);
            return new ResponseEntity<>(usuarioActualizado.getRoles(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Collections.singletonMap("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Error interno del servidor al desasignar rol: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}