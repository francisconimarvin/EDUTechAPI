// src/main/java/com/example/EdutechAPI/api/inscripciones/controller/InscripcionController.java
package com.example.EdutechAPI.api.inscripciones.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.EdutechAPI.api.inscripciones.dto.InscripcionRequest;
import com.example.EdutechAPI.api.inscripciones.dto.InscripcionResponse;
import com.example.EdutechAPI.api.inscripciones.model.Inscripcion;
import com.example.EdutechAPI.api.inscripciones.service.InscripcionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
// ---------------------------------

@Tag(name = "Inscripciones", description = "Gestión de inscripciones de usuarios a cursos.") // Anotación a nivel de clase
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @Autowired
    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @Operation(summary = "Obtener todas las inscripciones",
               description = "Recupera una lista de todas las inscripciones registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de inscripciones recuperada exitosamente",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = InscripcionResponse.class)))
    @GetMapping
    public ResponseEntity<List<InscripcionResponse>> getAllInscripciones() {
        List<InscripcionResponse> inscripciones = inscripcionService.getAllInscripciones();
        return ResponseEntity.ok(inscripciones);
    }

    @Operation(summary = "Obtener inscripción por ID",
               description = "Recupera los detalles de una inscripción específica utilizando su ID único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripción encontrada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = InscripcionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponse> getInscripcionById(
            @Parameter(description = "ID único de la inscripción a buscar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            InscripcionResponse inscripcion = inscripcionService.getInscripcionById(id);
            return ResponseEntity.ok(inscripcion);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear una nueva inscripción",
               description = "Registra una nueva inscripción de un usuario a un curso. Requiere 'idUsuario' e 'idCurso'. El estado inicial es 'Pendiente'.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inscripción creada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Inscripcion.class), // Aquí se devuelve el modelo completo, no el DTO
                     examples = @ExampleObject(name = "Inscripcion Exitosa", value = """
                         {
                             "idInscripcion": 101,
                             "usuario": { "idUsuario": 1, "nombre": "Jon Snow", ... },
                             "curso": { "idCurso": 10, "nombre": "Historia de Westeros", ... },
                             "fechaInscripcion": "2025-07-06T18:00:00Z",
                             "estado": "Pendiente"
                         }
                         """))),
        @ApiResponse(responseCode = "404", description = "Usuario o curso no encontrado",
                     content = @Content(mediaType = "application/json",
                     examples = @ExampleObject(name = "Usuario/Curso No Encontrado", value = "{}"))),
        @ApiResponse(responseCode = "409", description = "El usuario ya está inscrito en este curso",
                     content = @Content(mediaType = "application/json",
                     examples = @ExampleObject(name = "Conflicto", value = "{}")))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Objeto JSON con el ID del usuario y el ID del curso para la inscripción.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = InscripcionRequest.class),
            examples = @ExampleObject(name = "Solicitud de Inscripcion", value = """
                {
                    "idUsuario": 1,
                    "idCurso": 10
                }
                """)
        )
    )
    @PostMapping
    public ResponseEntity<Inscripcion> createInscripcion(@RequestBody InscripcionRequest request) {
        try {
            Inscripcion newInscripcion = inscripcionService.createInscripcion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newInscripcion);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @Operation(summary = "Actualizar el estado de una inscripción",
               description = "Modifica el estado de una inscripción existente (ej. 'Activo', 'Completado', 'Cancelado').")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de inscripción actualizado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Inscripcion.class))),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada",
                     content = @Content)
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<Inscripcion> updateInscripcionEstado(
            @Parameter(description = "ID de la inscripción a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado de la inscripción", required = true, example = "Completado")
            @RequestParam String nuevoEstado) {
        try {
            Inscripcion updatedInscripcion = inscripcionService.updateInscripcionEstado(id, nuevoEstado);
            return ResponseEntity.ok(updatedInscripcion);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar una inscripción completa",
               description = "Actualiza los detalles de una inscripción existente, incluyendo usuario y curso (si no hay conflicto).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripción actualizada exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Inscripcion.class))),
        @ApiResponse(responseCode = "404", description = "Inscripción, usuario o curso no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o conflicto (ej. inscripción ya existente)",
                     content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Objeto JSON con los nuevos IDs de usuario y curso para la inscripción.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = InscripcionRequest.class),
            examples = @ExampleObject(name = "Actualizar Inscripcion", value = """
                {
                    "idUsuario": 2,
                    "idCurso": 11
                }
                """)
        )
    )
    @PutMapping("/{id}")
    public ResponseEntity<Inscripcion> updateInscripcionCompleta(
            @Parameter(description = "ID de la inscripción a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody InscripcionRequest request) {
        try {
            Inscripcion updatedInscripcion = inscripcionService.updateInscripcionCompleta(id, request);
            return ResponseEntity.ok(updatedInscripcion);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Eliminar una inscripción",
               description = "Elimina una inscripción del sistema por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Inscripción eliminada exitosamente (No Content)"),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada",
                     content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInscripcion(
            @Parameter(description = "ID de la inscripción a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            inscripcionService.deleteInscripcion(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener inscripciones por ID de usuario",
               description = "Recupera una lista de todas las inscripciones asociadas a un ID de usuario específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripciones encontradas exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = InscripcionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado o sin inscripciones",
                     content = @Content)
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<InscripcionResponse>> getInscripcionesByUsuario(
            @Parameter(description = "ID del usuario para buscar sus inscripciones", required = true, example = "1")
            @PathVariable Long idUsuario) {
        try {
            List<InscripcionResponse> inscripciones = inscripcionService.getInscripcionesByUsuario(idUsuario);
            return ResponseEntity.ok(inscripciones);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener inscripciones por ID de curso",
               description = "Recupera una lista de todas las inscripciones asociadas a un ID de curso específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripciones encontradas exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = InscripcionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado o sin inscripciones",
                     content = @Content)
    })
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<InscripcionResponse>> getInscripcionesByCurso(
            @Parameter(description = "ID del curso para buscar sus inscripciones", required = true, example = "10")
            @PathVariable Long idCurso) {
        try {
            List<InscripcionResponse> inscripciones = inscripcionService.getInscripcionesByCurso(idCurso);
            return ResponseEntity.ok(inscripciones);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}