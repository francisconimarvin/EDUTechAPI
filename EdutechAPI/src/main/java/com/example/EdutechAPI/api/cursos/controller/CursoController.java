// src/main/java/com/example/EdutechAPI/api/cursos/controller/CursoController.java
package com.example.EdutechAPI.api.cursos.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.EdutechAPI.api.cursos.model.Curso;
import com.example.EdutechAPI.api.cursos.service.CursoService;

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

@Tag(name = "Cursos", description = "Gestión de cursos disponibles en la plataforma Edutech.") // Anotación a nivel de clase
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    @Autowired
    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @Operation(summary = "Obtener todos los cursos",
               description = "Recupera una lista de todos los cursos disponibles en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de cursos recuperada exitosamente",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = Curso.class, type = "array")))
    @GetMapping
    public ResponseEntity<List<Curso>> getAllCursos() {
        List<Curso> cursos = cursoService.getAllCursos();
        return ResponseEntity.ok(cursos);
    }

    @Operation(summary = "Obtener curso por ID",
               description = "Recupera los detalles de un curso específico utilizando su ID único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso encontrado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Curso.class))),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado",
                     content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Curso> getCursoById(
            @Parameter(description = "ID único del curso a buscar", required = true, example = "10")
            @PathVariable Long id) {
        return cursoService.getCursoById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo curso",
               description = "Registra un nuevo curso en el sistema. Se recomienda proporcionar un 'idCurso' único si no es autogenerado por la BD.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Curso creado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Curso.class),
                     examples = @ExampleObject(name = "Curso Creado", value = """
                         {
                             "idCurso": 1,
                             "nombre": "Introducción a la Magia",
                             "descripcion": "Un curso básico sobre los fundamentos de la magia.",
                             "duracionHoras": 40,
                             "precio": 99.99
                         }
                         """))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del nuevo curso a crear.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Curso.class),
            examples = @ExampleObject(name = "Ejemplo de Creación de Curso", value = """
                {
                    "nombre": "Introducción a la Magia",
                    "descripcion": "Un curso básico sobre los fundamentos de la magia.",
                    "duracionHoras": 40,
                    "precio": 99.99
                }
                """)
        )
    )
    @PostMapping
    public ResponseEntity<Curso> createCurso(@RequestBody Curso curso) {
        Curso newCurso = cursoService.createCurso(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCurso);
    }

    @Operation(summary = "Actualizar un curso existente",
               description = "Actualiza los detalles de un curso (nombre, descripción, duración, precio) por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente",
                     content = @Content(mediaType = "application/json",
                     schema = @Schema(implementation = Curso.class),
                     examples = @ExampleObject(name = "Curso Actualizado", value = """
                         {
                             "idCurso": 1,
                             "nombre": "Magia Avanzada",
                             "descripcion": "Profundiza en hechizos complejos.",
                             "duracionHoras": 60,
                             "precio": 149.99
                         }
                         """))),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado",
                     content = @Content),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                     content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del curso a actualizar.",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Curso.class),
            examples = @ExampleObject(name = "Ejemplo de Actualización de Curso", value = """
                {
                    "nombre": "Magia Avanzada",
                    "descripcion": "Profundiza en hechizos complejos.",
                    "duracionHoras": 60,
                    "precio": 149.99
                }
                """)
        )
    )
    @PutMapping("/{id}")
    public ResponseEntity<Curso> updateCurso(
            @Parameter(description = "ID del curso a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Curso cursoDetails) {
        try {
            Curso updatedCurso = cursoService.updateCurso(id, cursoDetails);
            return ResponseEntity.ok(updatedCurso);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar un curso",
               description = "Elimina un curso del sistema por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Curso eliminado exitosamente (No Content)"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado",
                     content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurso(
            @Parameter(description = "ID del curso a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            cursoService.deleteCurso(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}