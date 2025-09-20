package com.example.EdutechAPI.api.usuarios.service;

import com.example.EdutechAPI.api.usuarios.model.Usuario;
import com.example.EdutechAPI.api.usuarios.model.Rol;
import com.example.EdutechAPI.api.usuarios.repository.UsuarioRepository;
import com.example.EdutechAPI.api.usuarios.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
// import java.util.stream.Collectors; // Ya no necesario para esta parte específica

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        // Validación 1: Email único
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con este email: " + usuario.getEmail());
        }

        // --- VALIDACIÓN CRÍTICA PARA EL ID ---
        // Validación 2: Si el cliente proporciona un ID, verificar que no exista.
        if (usuario.getIdUsuario() != null) {
            if (usuarioRepository.existsById(usuario.getIdUsuario())) {
                throw new IllegalArgumentException("El ID de usuario " + usuario.getIdUsuario() + " ya existe. No se puede crear un usuario con un ID duplicado.");
            }
        }
        // --- FIN VALIDACIÓN CRÍTICA ---

        // Asignar fecha de registro si no viene (o asegurar que sea la actual)
        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(new Date());
        }
        // Asignar estado por defecto si no viene
        if (usuario.getEstado() == null || usuario.getEstado().isEmpty()) {
            usuario.setEstado("activo");
        }

        // Lógica para procesar roles enviados en la creación
        if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
            Set<Rol> rolesAsignar = new HashSet<>();
            for (Rol rolRecibido : usuario.getRoles()) {
                if (rolRecibido.getNombreRol() == null || rolRecibido.getNombreRol().isEmpty()) {
                     throw new IllegalArgumentException("El nombre del rol no puede ser nulo o vacío al asignar roles durante la creación.");
                }
                Rol rolExistente = rolRepository.findByNombreRol(rolRecibido.getNombreRol())
                    .orElseThrow(() -> new IllegalArgumentException("Rol '" + rolRecibido.getNombreRol() + "' no encontrado."));
                rolesAsignar.add(rolExistente);
            }
            usuario.setRoles(rolesAsignar);
        } else {
             usuario.setRoles(new HashSet<>());
        }

        return usuarioRepository.save(usuario);
    }

    // ... (El resto de métodos del servicio permanece igual) ...

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuarioDetalles) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            

            usuarioExistente.setNombre(usuarioDetalles.getNombre());
            usuarioExistente.setApellido(usuarioDetalles.getApellido());
            usuarioExistente.setEmail(usuarioDetalles.getEmail()); // Considerar validación de email único aquí también si se permite cambiar
            usuarioExistente.setEstado(usuarioDetalles.getEstado());
            usuarioExistente.setIdOficina(usuarioDetalles.getIdOficina());
            return usuarioRepository.save(usuarioExistente);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public Usuario asignarRolAUsuario(Long usuarioId, String nombreRol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol '" + nombreRol + "' no encontrado."));

        if (usuario.getRoles() == null) {
            usuario.setRoles(new HashSet<>());
        }

        if (usuario.getRoles().add(rol)) {
            return usuarioRepository.save(usuario);
        } else {
            return usuario;
        }
    }

    @Transactional
    public Usuario desasignarRolAUsuario(Long usuarioId, String nombreRol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol '" + nombreRol + "' no encontrado."));

        if (usuario.getRoles() != null && usuario.getRoles().remove(rol)) {
            return usuarioRepository.save(usuario);
        } else {
            return usuario;
        }
    }

    public Set<Rol> obtenerRolesDeUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(Usuario::getRoles)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
    }
}