// src/main/java/com/example/EdutechAPI/api/inscripciones/service/InscripcionService.java
package com.example.EdutechAPI.api.inscripciones.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <-- Añadir este import

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate; // Este import ya no será estrictamente necesario para los DTOs, pero lo dejo

import com.example.EdutechAPI.api.cursos.model.Curso;
import com.example.EdutechAPI.api.cursos.repository.CursoRepository;
import com.example.EdutechAPI.api.inscripciones.dto.InscripcionRequest;
import com.example.EdutechAPI.api.inscripciones.dto.InscripcionResponse; // <-- ¡IMPORTANTE! Añadir este import
import com.example.EdutechAPI.api.inscripciones.model.Inscripcion;
import com.example.EdutechAPI.api.inscripciones.repository.InscripcionRepository;
import com.example.EdutechAPI.api.usuarios.model.Usuario;
import com.example.EdutechAPI.api.usuarios.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    // --- Método auxiliar para mapear de Entidad a DTO ---
    private InscripcionResponse mapToInscripcionResponse(Inscripcion inscripcion) {
        // Asegúrate de que el usuario y el curso no sean null ANTES de intentar obtener sus IDs o nombres
        // Aunque la FK exista en la DB, si el objeto relacionado no se cargó, el proxy será null
        // Ojo: Si la relación permite null (nullable = true en @JoinColumn), un getUsuario() puede ser null.
        // Asumo que tus FKs son NOT NULL (nullable = false).
        
        Long idUsuario = null;
        String nombreUsuario = null;
        if (inscripcion.getUsuario() != null) {
            idUsuario = inscripcion.getUsuario().getIdUsuario();
            nombreUsuario = inscripcion.getUsuario().getNombre() + " " + inscripcion.getUsuario().getApellido();
        }

        Long idCurso = null;
        String nombreCurso = null;
        if (inscripcion.getCurso() != null) {
            idCurso = inscripcion.getCurso().getIdCurso();
            nombreCurso = inscripcion.getCurso().getNombreCurso();
        }

        return new InscripcionResponse(
                inscripcion.getIdInscripcion(),
                idUsuario,
                nombreUsuario,
                idCurso,
                nombreCurso,
                inscripcion.getFechaInscripcion(),
                inscripcion.getEstado()
        );
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponse> getAllInscripciones() { // Cambiado para devolver DTO
        List<Inscripcion> inscripciones = inscripcionRepository.findAll();
        return inscripciones.stream()
                .map(this::mapToInscripcionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InscripcionResponse getInscripcionById(Long id) { // Cambiado para devolver DTO
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inscripción no encontrada con ID: " + id));
        return mapToInscripcionResponse(inscripcion);
    }

    // --- Métodos de escritura (POST, PUT, DELETE) que manejan la entidad Inscripcion ---
    // Estos métodos devuelven la entidad Inscripcion directamente. Si quieres que también devuelvan DTOs,
    // tendrías que cambiar su tipo de retorno a InscripcionResponse y mapear el resultado antes de devolverlo.
    // Pero para que los GETs funcionen con IDs, lo principal es cambiar los GETs.

    @Transactional
    public Inscripcion createInscripcion(InscripcionRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + request.getIdUsuario()));

        Curso curso = cursoRepository.findById(request.getIdCurso())
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + request.getIdCurso()));

        if (inscripcionRepository.findByUsuarioIdUsuarioAndCursoIdCurso(request.getIdUsuario(), request.getIdCurso()).isPresent()) {
            throw new IllegalArgumentException("El usuario con ID " + request.getIdUsuario() + " ya está inscrito en el curso con ID " + request.getIdCurso());
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setUsuario(usuario);
        inscripcion.setCurso(curso);
        inscripcion.setFechaInscripcion(new Date());
        inscripcion.setEstado("en curso");

        return inscripcionRepository.save(inscripcion);
    }

    @Transactional
    public Inscripcion updateInscripcionEstado(Long id, String nuevoEstado) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inscripción no encontrada con ID: " + id));

        inscripcion.setEstado(nuevoEstado);

        return inscripcionRepository.save(inscripcion);
    }

    @Transactional
    public Inscripcion updateInscripcionCompleta(Long id, InscripcionRequest request) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inscripción no encontrada con ID: " + id));

        if (request.getIdUsuario() != null && !request.getIdUsuario().equals(inscripcion.getUsuario().getIdUsuario())) {
            Usuario newUsuario = usuarioRepository.findById(request.getIdUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Nuevo Usuario no encontrado con ID: " + request.getIdUsuario()));
            inscripcion.setUsuario(newUsuario);
        }

        if (request.getIdCurso() != null && !request.getIdCurso().equals(inscripcion.getCurso().getIdCurso())) {
            Curso newCurso = cursoRepository.findById(request.getIdCurso())
                    .orElseThrow(() -> new EntityNotFoundException("Nuevo Curso no encontrado con ID: " + request.getIdCurso()));
            inscripcion.setCurso(newCurso);
        }

        if (request.getIdUsuario() != null && request.getIdCurso() != null &&
            inscripcionRepository.findByUsuarioIdUsuarioAndCursoIdCurso(request.getIdUsuario(), request.getIdCurso())
                                 .filter(i -> !i.getIdInscripcion().equals(id))
                                 .isPresent()) {
            throw new IllegalArgumentException("Ya existe una inscripción para el usuario con ID " + request.getIdUsuario() + " en el curso con ID " + request.getIdCurso() + ".");
        }

        if (request.getFechaInscripcion() != null) {
            inscripcion.setFechaInscripcion(request.getFechaInscripcion());
        }

        if (request.getEstado() != null && !request.getEstado().isEmpty()) {
            inscripcion.setEstado(request.getEstado());
        }

        return inscripcionRepository.save(inscripcion);
    }

    @Transactional
    public void deleteInscripcion(Long id) {
        if (!inscripcionRepository.existsById(id)) {
            throw new EntityNotFoundException("Inscripción no encontrada con ID: " + id);
        }
        inscripcionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponse> getInscripcionesByUsuario(Long idUsuario) { // Cambiado para devolver DTO
        usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario));
        
        List<Inscripcion> inscripciones = inscripcionRepository.findByUsuario_IdUsuario(idUsuario);
        
        return inscripciones.stream()
                .map(this::mapToInscripcionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponse> getInscripcionesByCurso(Long idCurso) { // Cambiado para devolver DTO
        cursoRepository.findById(idCurso)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + idCurso));
        
        List<Inscripcion> inscripciones = inscripcionRepository.findByCurso_IdCurso(idCurso);

        return inscripciones.stream()
                .map(this::mapToInscripcionResponse)
                .collect(Collectors.toList());
    }
}