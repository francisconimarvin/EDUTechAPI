package com.example.EdutechAPI.api.inscripciones.repository;

import com.example.EdutechAPI.api.inscripciones.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ¡Importar Query!
import org.springframework.data.repository.query.Param; // ¡Importar Param!
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // --- ¡CAMBIO CRÍTICO AQUÍ! Usamos una consulta JPQL explícita ---
    @Query("SELECT i FROM Inscripcion i WHERE i.usuario.idUsuario = :idUsuario AND i.curso.idCurso = :idCurso")
    Optional<Inscripcion> findByUsuarioIdUsuarioAndCursoIdCurso(@Param("idUsuario") Long idUsuario, @Param("idCurso") Long idCurso);

    // Los otros métodos no necesitan @Query si siguen las convenciones de Spring Data JPA
    List<Inscripcion> findByUsuario_IdUsuario(Long idUsuario);
    List<Inscripcion> findByCurso_IdCurso(Long idCurso);
}