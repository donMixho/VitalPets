package com.vitalpets.citas.service;

import com.vitalpets.citas.dto.CitaDto;
import com.vitalpets.citas.model.Cita;
import com.vitalpets.citas.model.EstadoCita;
import com.vitalpets.citas.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;

    public CitaDto registrar(CitaDto dto) {
        return toDto(citaRepository.save(toEntity(dto)));
    }

    public List<CitaDto> listarTodas() {
        return citaRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public CitaDto buscarPorId(Long id) {
        return toDto(citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id)));
    }

    public List<CitaDto> porCliente(Long clienteId) {
        return citaRepository.findByClienteId(clienteId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<CitaDto> porEstado(EstadoCita estado) {
        return citaRepository.findByEstado(estado)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public CitaDto cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Cita c = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));
        c.setEstado(nuevoEstado);
        return toDto(citaRepository.save(c));
    }

    public void cancelar(Long id) {
        cambiarEstado(id, EstadoCita.CANCELADA);
    }

    private CitaDto toDto(Cita c) {
        return CitaDto.builder()
                .id(c.getId()).mascotaId(c.getMascotaId()).clienteId(c.getClienteId())
                .terceroId(c.getTerceroId()).personalId(c.getPersonalId())
                .fechaHora(c.getFechaHora()).tipoServicio(c.getTipoServicio())
                .estado(c.getEstado()).observaciones(c.getObservaciones())
                .nombreQuienTrae(c.getNombreQuienTrae()).build();
    }

    private Cita toEntity(CitaDto dto) {
        return Cita.builder()
                .mascotaId(dto.getMascotaId()).clienteId(dto.getClienteId())
                .terceroId(dto.getTerceroId()).personalId(dto.getPersonalId())
                .fechaHora(dto.getFechaHora()).tipoServicio(dto.getTipoServicio())
                .observaciones(dto.getObservaciones())
                .nombreQuienTrae(dto.getNombreQuienTrae()).build();
    }
}