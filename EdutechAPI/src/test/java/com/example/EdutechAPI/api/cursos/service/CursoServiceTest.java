package com.example.EdutechAPI.api.cursos.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet; 
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.EdutechAPI.api.cursos.model.Curso;
import com.example.EdutechAPI.api.cursos.repository.CursoRepository; 

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    // --- Prueba para `createCurso()` ---
    @Test
    void cuandoCreateCurso_conDatosValidos_debeRetornarCursoGuardado() {
        // Arrange
        // Constructor de Curso (6 parámetros, según tu modelo):
        // (Long idCurso, String nombreCurso, String descripcion, Date fechaCreacion, String estado, Set<Inscripcion> inscripciones)
        Curso nuevoCurso = new Curso(null, "Programacion Java", "Curso introductorio", null, null, new HashSet<>());
        
        // Simulamos el curso que el repositorio devolvería con ID, fecha y estado asignados
        Curso cursoGuardadoSimulado = new Curso(1L, "Programacion Java", "Curso introductorio", new Date(), "activo", new HashSet<>());

        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoGuardadoSimulado);

        // Act
        Curso resultado = cursoService.createCurso(nuevoCurso);

        // Assert
        assertNotNull(resultado, "El curso guardado no debería ser nulo.");
        assertNotNull(resultado.getIdCurso(), "El ID del curso debería ser asignado.");
        assertEquals("Programacion Java", resultado.getNombreCurso(), "El nombre del curso no coincide.");
        assertEquals("activo", resultado.getEstado(), "El estado debería ser 'activo' por defecto.");
        assertNotNull(resultado.getFechaCreacion(), "La fecha de creación debería ser asignada.");

        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    void cuandoCreateCurso_conEstadoYFechaExistentes_debeRespetarlos() {
        // Arrange
        Date fechaFija = new Date(System.currentTimeMillis() - 86400000); // Ayer
        Curso nuevoCurso = new Curso(null, "Diseño Web", "Curso de HTML/CSS", fechaFija, "borrador", new HashSet<>());
        
        Curso cursoGuardadoSimulado = new Curso(1L, "Diseño Web", "Curso de HTML/CSS", fechaFija, "borrador", new HashSet<>());

        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoGuardadoSimulado);

        // Act
        Curso resultado = cursoService.createCurso(nuevoCurso);

        // Assert
        assertNotNull(resultado);
        assertEquals("borrador", resultado.getEstado(), "El estado existente debería ser respetado.");
        assertEquals(fechaFija, resultado.getFechaCreacion(), "La fecha existente debería ser respetada.");

        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    // --- Prueba para `getCursoById()` ---
    @Test
    void cuandoGetCursoById_conIdExistente_debeRetornarOptionalConCurso() {
        // Arrange
        Long idExistente = 1L;
        Curso cursoSimulado = new Curso(idExistente, "Algebra Lineal", "Matematicas", new Date(), "activo", new HashSet<>());
        when(cursoRepository.findById(idExistente)).thenReturn(Optional.of(cursoSimulado));

        // Act
        Optional<Curso> resultado = cursoService.getCursoById(idExistente);

        // Assert
        assertTrue(resultado.isPresent(), "El curso debería estar presente.");
        assertEquals(idExistente, resultado.get().getIdCurso(), "El ID del curso no coincide.");
        assertEquals("Algebra Lineal", resultado.get().getNombreCurso(), "El nombre del curso no coincide.");

        verify(cursoRepository, times(1)).findById(idExistente);
    }

    @Test
    void cuandoGetCursoById_conIdNoExistente_debeRetornarOptionalVacio() {
        // Arrange
        Long idNoExistente = 99L;
        when(cursoRepository.findById(idNoExistente)).thenReturn(Optional.empty());

        // Act
        Optional<Curso> resultado = cursoService.getCursoById(idNoExistente);

        // Assert
        assertFalse(resultado.isPresent(), "El curso no debería estar presente.");
        verify(cursoRepository, times(1)).findById(idNoExistente);
    }

    // --- Prueba para `getAllCursos()` ---
    @Test
    void cuandoGetAllCursos_debeRetornarListaDeCursos() {
        // Arrange
        Curso curso1 = new Curso(1L, "Física", "Intro a la Física", new Date(), "activo", new HashSet<>());
        Curso curso2 = new Curso(2L, "Química", "Química Orgánica", new Date(), "activo", new HashSet<>());
        List<Curso> cursosSimulados = List.of(curso1, curso2);

        when(cursoRepository.findAll()).thenReturn(cursosSimulados);

        // Act
        List<Curso> resultado = cursoService.getAllCursos();

        // Assert
        assertNotNull(resultado, "La lista de cursos no debería ser nula.");
        assertEquals(2, resultado.size(), "Deberían retornar 2 cursos.");
        assertEquals("Física", resultado.get(0).getNombreCurso(), "El nombre del primer curso no coincide.");

        verify(cursoRepository, times(1)).findAll();
    }

    @Test
    void cuandoGetAllCursos_debeRetornarListaVaciaSiNoHayCursos() {
        // Arrange
        when(cursoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Curso> resultado = cursoService.getAllCursos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(cursoRepository, times(1)).findAll();
    }

    // --- Prueba para `updateCurso()` ---
    @Test
    void cuandoUpdateCurso_conIdExistente_debeActualizarYRetornarCurso() {
        // Arrange
        Long idExistente = 1L;
        Curso cursoExistente = new Curso(idExistente, "Curso Original", "Descripción Original", new Date(), "activo", new HashSet<>());
        Curso cursoDetails = new Curso(null, "Curso Actualizado", "Descripción Nueva", null, "inactivo", null); // Los null serán ignorados por el setter

        when(cursoRepository.findById(idExistente)).thenReturn(Optional.of(cursoExistente));
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoExistente); // save devuelve la misma instancia modificada

        // Act
        Curso resultado = cursoService.updateCurso(idExistente, cursoDetails);

        // Assert
        assertNotNull(resultado);
        assertEquals(idExistente, resultado.getIdCurso());
        assertEquals("Curso Actualizado", resultado.getNombreCurso());
        assertEquals("Descripción Nueva", resultado.getDescripcion());
        assertEquals("inactivo", resultado.getEstado());

        verify(cursoRepository, times(1)).findById(idExistente);
        verify(cursoRepository, times(1)).save(cursoExistente); // Verificar que se guardó la instancia modificada
    }

    @Test
    void cuandoUpdateCurso_conIdNoExistente_debeLanzarExcepcion() {
        // Arrange
        Long idNoExistente = 99L;
        Curso cursoDetails = new Curso(null, "Nombre", "Desc", null, "estado", null);
        when(cursoRepository.findById(idNoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            cursoService.updateCurso(idNoExistente, cursoDetails);
        });
        assertEquals("Curso no encontrado con ID: " + idNoExistente, exception.getMessage());

        verify(cursoRepository, times(1)).findById(idNoExistente);
        verify(cursoRepository, never()).save(any(Curso.class)); // save no debería ser llamado
    }

    // --- Prueba para `deleteCurso()` ---
    @Test
    void cuandoDeleteCurso_conIdExistente_debeEliminarCurso() {
        // Arrange
        Long idExistente = 1L;
        when(cursoRepository.existsById(idExistente)).thenReturn(true);

        // Act
        cursoService.deleteCurso(idExistente);

        // Assert
        verify(cursoRepository, times(1)).existsById(idExistente);
        verify(cursoRepository, times(1)).deleteById(idExistente);
    }

    @Test
    void cuandoDeleteCurso_conIdNoExistente_debeLanzarExcepcion() {
        // Arrange
        Long idNoExistente = 99L;
        when(cursoRepository.existsById(idNoExistente)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            cursoService.deleteCurso(idNoExistente);
        });
        assertEquals("Curso no encontrado con ID: " + idNoExistente, exception.getMessage());

        verify(cursoRepository, times(1)).existsById(idNoExistente);
        verify(cursoRepository, never()).deleteById(anyLong()); // deleteById no debería ser llamado
    }
}