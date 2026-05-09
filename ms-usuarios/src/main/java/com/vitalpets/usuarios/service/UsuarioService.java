package com.vitalpets.usuarios.service;

import com.vitalpets.usuarios.dto.UsuarioDto;
import com.vitalpets.usuarios.model.Usuario;
import com.vitalpets.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDto registrar(UsuarioDto dto) {
        return toDto(usuarioRepository.save(toEntity(dto)));
    }

    public List<UsuarioDto> listarActivos() {
        return usuarioRepository.findByActivoTrue()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public UsuarioDto buscarPorId(Long id) {
        return toDto(usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id)));
    }

    public UsuarioDto login(String username, String password) {
        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!u.getPassword().equals(password))
            throw new RuntimeException("Contraseña incorrecta");
        return toDto(u);
    }

    public void desactivar(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        u.setActivo(false);
        usuarioRepository.save(u);
    }

    private UsuarioDto toDto(Usuario u) {
        return UsuarioDto.builder()
                .id(u.getId()).username(u.getUsername())
                .nombreCompleto(u.getNombreCompleto())
                .rol(u.getRol()).activo(u.getActivo()).build();
    }

    private Usuario toEntity(UsuarioDto dto) {
        return Usuario.builder()
                .username(dto.getUsername()).password(dto.getPassword())
                .nombreCompleto(dto.getNombreCompleto()).rol(dto.getRol()).build();
    }
}