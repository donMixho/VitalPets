package com.vitalpets.mascotas.service;

import com.vitalpets.mascotas.dto.MascotaDto;
import com.vitalpets.mascotas.model.Especie;
import com.vitalpets.mascotas.model.Mascota;
import com.vitalpets.mascotas.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok: inyecta el repository por constructor automáticamente
public class MascotaService {

    private final MascotaRepository mascotaRepository;

    // ── REGISTRAR mascota nueva ──────────────────────────────
    public MascotaDto registrar(MascotaDto dto) {
        Mascota mascota = toEntity(dto);
        return toDto(mascotaRepository.save(mascota));
    }

    // ── LISTAR todas las mascotas activas ────────────────────
    public List<MascotaDto> listarActivas() {
        return mascotaRepository.findByActivoTrue()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── BUSCAR por ID ────────────────────────────────────────
    public MascotaDto buscarPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + id));
        return toDto(mascota);
    }

    // ── BUSCAR por dueño ─────────────────────────────────────
    public List<MascotaDto> buscarPorCliente(Long clienteId) {
        return mascotaRepository.findByClienteId(clienteId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── BUSCAR por especie ───────────────────────────────────
    public List<MascotaDto> buscarPorEspecie(Especie especie) {
        return mascotaRepository.findByEspecie(especie)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ── ACTUALIZAR ───────────────────────────────────────────
    public MascotaDto actualizar(Long id, MascotaDto dto) {
        Mascota existente = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + id));
        existente.setNombre(dto.getNombre());
        existente.setRaza(dto.getRaza());
        existente.setEdadAnios(dto.getEdadAnios());
        existente.setPesoKg(dto.getPesoKg());
        existente.setNotasEspecie(dto.getNotasEspecie());
        return toDto(mascotaRepository.save(existente));
    }

    // ── DESACTIVAR (borrado lógico, no físico) ───────────────
    public void desactivar(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + id));
        mascota.setActivo(false);
        mascotaRepository.save(mascota);
    }

    // ── Conversores privados Entity ↔ DTO ────────────────────
    private MascotaDto toDto(Mascota m) {
        return MascotaDto.builder()
                .id(m.getId()).nombre(m.getNombre()).especie(m.getEspecie())
                .raza(m.getRaza()).edadAnios(m.getEdadAnios()).sexo(m.getSexo())
                .pesoKg(m.getPesoKg()).colorPelaje(m.getColorPelaje())
                .notasEspecie(m.getNotasEspecie()).clienteId(m.getClienteId())
                .activo(m.getActivo()).build();
    }

    private Mascota toEntity(MascotaDto dto) {
        return Mascota.builder()
                .nombre(dto.getNombre()).especie(dto.getEspecie())
                .raza(dto.getRaza()).edadAnios(dto.getEdadAnios()).sexo(dto.getSexo())
                .pesoKg(dto.getPesoKg()).colorPelaje(dto.getColorPelaje())
                .notasEspecie(dto.getNotasEspecie()).clienteId(dto.getClienteId())
                .build();
    }
}