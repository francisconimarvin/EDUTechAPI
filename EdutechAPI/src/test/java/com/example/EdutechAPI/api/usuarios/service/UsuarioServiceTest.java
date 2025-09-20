package com.example.EdutechAPI.api.usuarios.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet; 
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.EdutechAPI.api.usuarios.model.Usuario;
import com.example.EdutechAPI.api.usuarios.repository.RolRepository;
import com.example.EdutechAPI.api.usuarios.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void cuandoCrearUsuario_conDatosValidos_debeRetornarUsuarioGuardado() {
        // Arrange (Preparación)
        // Constructor de Usuario:
        // (Long idUsuario, String nombre, String apellido, String email, String contrasena,
        //  Date fechaRegistro, String estado, Long idOficina, Set<Rol> roles, Set<Inscripcion> inscripciones)

        Usuario nuevoUsuario = new Usuario(
            null,               // idUsuario (se generará)
            "Nuevo",            // nombre
            "Usuario",          // apellido
            "nuevo@mail.com",   // email
            "password",         // contrasena
            null,               // fechaRegistro (se asignará en el servicio)
            null,               // estado (se asignará en el servicio)
            null,               // idOficina (opcional, si es null no hay problema)
            new HashSet<>(),    // roles (inicializar un Set vacío)
            new HashSet<>()    // inscripciones (inicializar un Set vacío)
        );

        // Simulamos el usuario que el repositorio devolvería después de "guardar"
        Usuario usuarioGuardadoSimulado = new Usuario(
            1L,                 // idUsuario ya asignado
            "Nuevo",            // nombre
            "Usuario",          // apellido
            "nuevo@mail.com",   // email
            "password",         // contrasena
            new Date(),         // fechaRegistro ya asignada
            "activo",           // estado ya asignado
            null,               // idOficina
            new HashSet<>(),    // roles
            new HashSet<>()    // inscripciones
        );

        // Definimos el comportamiento de los mocks:
        when(usuarioRepository.findByEmail(nuevoUsuario.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardadoSimulado);

        // Act (Ejecución)
        Usuario resultado = usuarioService.crearUsuario(nuevoUsuario);

        // Assert (Verificación)
        assertNotNull(resultado, "El usuario guardado no debería ser nulo.");
        assertNotNull(resultado.getIdUsuario(), "El ID del usuario debería ser asignado.");
        assertEquals("nuevo@mail.com", resultado.getEmail(), "El email no coincide.");
        assertEquals("Nuevo", resultado.getNombre(), "El nombre no coincide.");
        assertEquals("activo", resultado.getEstado(), "El estado debería ser 'activo' por defecto.");
        assertNotNull(resultado.getFechaRegistro(), "La fecha de registro debería ser asignada.");
        assertTrue(resultado.getRoles().isEmpty(), "No debería tener roles si no se especificaron.");

        // Verificamos que los métodos del repositorio fueron llamados.
        verify(usuarioRepository, times(1)).findByEmail(nuevoUsuario.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verifyNoInteractions(rolRepository);
    }

    @Test
    void cuandoObtenerUsuarioPorId_conIdExistente_debeRetornarUsuario() {
        // Arrange
        Long idExistente = 1L;
        Usuario usuarioSimulado = new Usuario(
            idExistente,
            "Nombre",
            "Apellido",
            "existente@mail.com",
            "pass",
            new Date(),
            "activo",
            100L,
            new HashSet<>(),
            new HashSet<>()
        );

        when(usuarioRepository.findById(idExistente)).thenReturn(Optional.of(usuarioSimulado));

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(idExistente);

        // Assert
        assertTrue(resultado.isPresent(), "El usuario debería estar presente.");
        assertEquals(idExistente, resultado.get().getIdUsuario(), "El ID del usuario no coincide.");
        assertEquals("Nombre", resultado.get().getNombre(), "El nombre del usuario no coincide.");

        verify(usuarioRepository, times(1)).findById(idExistente);
        verifyNoInteractions(rolRepository);
    }

    @Test
    void cuandoObtenerUsuarioPorId_conIdNoExistente_debeRetornarOptionalVacio() {
        // Arrange
        Long idNoExistente = 99L;
        when(usuarioRepository.findById(idNoExistente)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(idNoExistente);

        // Assert
        assertFalse(resultado.isPresent(), "El usuario no debería estar presente.");
        verify(usuarioRepository, times(1)).findById(idNoExistente);
        verifyNoInteractions(rolRepository);
    }

    @Test
    void cuandoObtenerTodosLosUsuarios_debeRetornarListaDeUsuarios() {
        // Arrange
        Usuario usuario1 = new Usuario(
            1L, "Nombre1", "Apellido1", "user1@example.com", "pass1", new Date(), "activo", 101L, new HashSet<>(), new HashSet<>()
        );
        Usuario usuario2 = new Usuario(
            2L, "Nombre2", "Apellido2", "user2@example.com", "pass2", new Date(), "activo", 102L, new HashSet<>(), new HashSet<>()
        );
        List<Usuario> usuariosSimulados = List.of(usuario1, usuario2);

        when(usuarioRepository.findAll()).thenReturn(usuariosSimulados);

        // Act
        List<Usuario> resultado = usuarioService.obtenerTodosLosUsuarios();

        // Assert
        assertNotNull(resultado, "La lista de usuarios no debería ser nula.");
        assertEquals(2, resultado.size(), "Deberían retornar 2 usuarios.");
        assertEquals("user1@example.com", resultado.get(0).getEmail(), "El email del primer usuario no coincide.");

        verify(usuarioRepository, times(1)).findAll();
        verifyNoInteractions(rolRepository);
    }

    @Test
    void cuandoObtenerTodosLosUsuarios_debeRetornarListaVaciaSiNoHayUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Usuario> resultado = usuarioService.obtenerTodosLosUsuarios();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(usuarioRepository, times(1)).findAll();
        verifyNoInteractions(rolRepository);
    }
}