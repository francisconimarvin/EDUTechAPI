package com.example.EdutechAPI.api.cursos.service;

import com.example.EdutechAPI.api.cursos.model.Curso;
import com.example.EdutechAPI.api.cursos.repository.CursoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    @Autowired
    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> getAllCursos() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> getCursoById(Long id) {
        return cursoRepository.findById(id);
    }

    @Transactional
    public Curso createCurso(Curso curso) {
        if (curso.getFechaCreacion() == null) {
            curso.setFechaCreacion(new Date());
        }
        if (curso.getEstado() == null || curso.getEstado().isEmpty()) {
            curso.setEstado("activo");
        }
        return cursoRepository.save(curso);
    }

    @Transactional
    public Curso updateCurso(Long id, Curso cursoDetails) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + id));

        curso.setNombreCurso(cursoDetails.getNombreCurso());
        curso.setDescripcion(cursoDetails.getDescripcion());
        curso.setEstado(cursoDetails.getEstado());

        return cursoRepository.save(curso);
    }

    @Transactional
    public void deleteCurso(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new EntityNotFoundException("Curso no encontrado con ID: " + id);
        }
        cursoRepository.deleteById(id);
    }
}