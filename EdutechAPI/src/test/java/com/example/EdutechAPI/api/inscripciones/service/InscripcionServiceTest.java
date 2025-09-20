package com.example.EdutechAPI.api.inscripciones.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.EdutechAPI.api.cursos.model.Curso;
import com.example.EdutechAPI.api.cursos.repository.CursoRepository;
import com.example.EdutechAPI.api.inscripciones.dto.InscripcionRequest;
import com.example.EdutechAPI.api.inscripciones.dto.InscripcionResponse;
import com.example.EdutechAPI.api.inscripciones.model.Inscripcion;
import com.example.EdutechAPI.api.inscripciones.repository.InscripcionRepository;
import com.example.EdutechAPI.api.usuarios.model.Usuario;
import com.example.EdutechAPI.api.usuarios.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private InscripcionService inscripcionService;

    @Test
    void cuandoCreateInscripcion_conDatosValidos_debeRetornarInscripcionCreada() {
        // Arrange
        Long usuarioId = 1L;
        Long cursoId = 10L;

        // Constructor de Usuario (10 parámetros):
        // (Long idUsuario, String nombre, String apellido, String email, String contrasena, Date fechaRegistro, String estado, Long idOficina, Set<Rol> roles, Set<Inscripcion> inscripciones)
        Usuario usuarioMock = new Usuario(usuarioId, "Juan", "Perez", "juan@mail.com", "pass", new Date(), "activo", 100L, new HashSet<>(), new HashSet<>());

        // Constructor de Curso (6 parámetros, según tu modelo exacto):
        // (Long idCurso, String nombreCurso, String descripcion, Date fechaCreacion, String estado, Set<Inscripcion> inscripciones)
        Curso cursoMock = new Curso(cursoId, "Matematicas", "Curso de Matematicas avanzado", new Date(), "activo", new HashSet<>());

        InscripcionRequest request = new InscripcionRequest(usuarioId, cursoId, null, null);

        // Simulamos la inscripción que el repositorio devolverá
        // Constructor de Inscripcion (5 parámetros):
        // (Long idInscripcion, Usuario usuario, Curso curso, Date fechaInscripcion, String estado)
        Inscripcion inscripcionGuardada = new Inscripcion(1L, usuarioMock, cursoMock, new Date(), "en curso");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioMock));
        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(cursoMock));
        when(inscripcionRepository.findByUsuarioIdUsuarioAndCursoIdCurso(usuarioId, cursoId)).thenReturn(Optional.empty());
        when(inscripcionRepository.save(any(Inscripcion.class))).thenReturn(inscripcionGuardada);

        // Act
        Inscripcion resultado = inscripcionService.createInscripcion(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdInscripcion());
        assertEquals(usuarioId, resultado.getUsuario().getIdUsuario());
        assertEquals(cursoId, resultado.getCurso().getIdCurso());
        assertEquals("en curso", resultado.getEstado());

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(cursoRepository, times(1)).findById(cursoId);
        verify(inscripcionRepository, times(1)).findByUsuarioIdUsuarioAndCursoIdCurso(usuarioId, cursoId);
        verify(inscripcionRepository, times(1)).save(any(Inscripcion.class));
    }

    @Test
    void cuandoCreateInscripcion_conUsuarioNoEncontrado_debeLanzarExcepcion() {
        // Arrange
        Long usuarioId = 1L;
        Long cursoId = 10L;
        InscripcionRequest request = new InscripcionRequest(usuarioId, cursoId, null, null);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty()); // Usuario no encontrado

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            inscripcionService.createInscripcion(request);
        });
        assertEquals("Usuario no encontrado con ID: " + usuarioId, exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verifyNoInteractions(cursoRepository); // No se debería llamar a cursoRepository
        verifyNoInteractions(inscripcionRepository); // Ni a inscripcionRepository
    }

    @Test
    void cuandoCreateInscripcion_conInscripcionExistente_debeLanzarExcepcion() {
        // Arrange
        Long usuarioId = 1L;
        Long cursoId = 10L;
        // Constructores ajustados para que coincidan con tus modelos
        Usuario usuarioMock = new Usuario(usuarioId, "Juan", "Perez", "juan@mail.com", "pass", new Date(), "activo", 100L, new HashSet<>(), new HashSet<>());
        Curso cursoMock = new Curso(cursoId, "Matematicas", "Curso de Matematicas avanzado", new Date(), "activo", new HashSet<>());
        
        InscripcionRequest request = new InscripcionRequest(usuarioId, cursoId, null, null);

        Inscripcion inscripcionExistente = new Inscripcion(5L, usuarioMock, cursoMock, new Date(), "en curso");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioMock));
        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(cursoMock));
        when(inscripcionRepository.findByUsuarioIdUsuarioAndCursoIdCurso(usuarioId, cursoId)).thenReturn(Optional.of(inscripcionExistente)); // Ya existe

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            inscripcionService.createInscripcion(request);
        });
        assertEquals("El usuario con ID " + usuarioId + " ya está inscrito en el curso con ID " + cursoId, exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(cursoRepository, times(1)).findById(cursoId);
        verify(inscripcionRepository, times(1)).findByUsuarioIdUsuarioAndCursoIdCurso(usuarioId, cursoId);
        verify(inscripcionRepository, never()).save(any(Inscripcion.class)); // El save no debería ser llamado
    }


    // --- Prueba para `getInscripcionById()` ---
    @Test
    void cuandoGetInscripcionById_conIdExistente_debeRetornarInscripcionResponse() {
        // Arrange
        Long idExistente = 1L;
        // Constructores ajustados para que coincidan con tus modelos
        Usuario usuarioMock = new Usuario(10L, "Laura", "Diaz", "laura@mail.com", "pass", new Date(), "activo", 100L, new HashSet<>(), new HashSet<>());
        Curso cursoMock = new Curso(20L, "Historia", "Curso de Historia Antigua", new Date(), "activo", new HashSet<>());
        
        Inscripcion inscripcionSimulada = new Inscripcion(idExistente, usuarioMock, cursoMock, new Date(), "finalizada");

        when(inscripcionRepository.findById(idExistente)).thenReturn(Optional.of(inscripcionSimulada));

        // Act
        InscripcionResponse resultado = inscripcionService.getInscripcionById(idExistente);

        // Assert
        assertNotNull(resultado);
        assertEquals(idExistente, resultado.getIdInscripcion());
        assertEquals(usuarioMock.getIdUsuario(), resultado.getIdUsuario());
        assertEquals(usuarioMock.getNombre() + " " + usuarioMock.getApellido(), resultado.getNombreUsuario());
        assertEquals(cursoMock.getIdCurso(), resultado.getIdCurso());
        assertEquals(cursoMock.getNombreCurso(), resultado.getNombreCurso());
        assertEquals("finalizada", resultado.getEstado());

        verify(inscripcionRepository, times(1)).findById(idExistente);
        verifyNoInteractions(usuarioRepository); // No debería interactuar con otros repositorios para un GET by ID
        verifyNoInteractions(cursoRepository);
    }

    @Test
    void cuandoGetInscripcionById_conIdNoExistente_debeLanzarExcepcion() {
        // Arrange
        Long idNoExistente = 99L;
        when(inscripcionRepository.findById(idNoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            inscripcionService.getInscripcionById(idNoExistente);
        });
        assertEquals("Inscripción no encontrada con ID: " + idNoExistente, exception.getMessage());

        verify(inscripcionRepository, times(1)).findById(idNoExistente);
    }
}