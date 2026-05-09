package com.vitalpets.historial.service;

import com.vitalpets.historial.dto.HistorialDto;
import com.vitalpets.historial.model.HistorialMedico;
import com.vitalpets.historial.model.TipoEvento;
import com.vitalpets.historial.repository.HistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistorialService {

    private final HistorialRepository historialRepository;

    public HistorialDto registrar(HistorialDto dto) {
        return toDto(historialRepository.save(toEntity(dto)));
    }

    public List<HistorialDto> listarTodos() {
        return historialRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<HistorialDto> porMascota(Long mascotaId) {
        return historialRepository.findByMascotaIdOrderByFechaEventoDesc(mascotaId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<HistorialDto> porCliente(Long clienteId) {
        return historialRepository.findByClienteId(clienteId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public HistorialDto buscarPorId(Long id) {
        return toDto(historialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado: " + id)));
    }

    private HistorialDto toDto(HistorialMedico h) {
        return HistorialDto.builder()
                .id(h.getId()).mascotaId(h.getMascotaId()).clienteId(h.getClienteId())
                .personalId(h.getPersonalId()).citaId(h.getCitaId())
                .tipoEvento(h.getTipoEvento()).fechaEvento(h.getFechaEvento())
                .descripcion(h.getDescripcion()).diagnostico(h.getDiagnostico())
                .tratamiento(h.getTratamiento()).medicamentosRecetados(h.getMedicamentosRecetados())
                .proximaVisita(h.getProximaVisita()).nombreQuienTrajo(h.getNombreQuienTrajo()).build();
    }

    private HistorialMedico toEntity(HistorialDto dto) {
        return HistorialMedico.builder()
                .mascotaId(dto.getMascotaId()).clienteId(dto.getClienteId())
                .personalId(dto.getPersonalId()).citaId(dto.getCitaId())
                .tipoEvento(dto.getTipoEvento()).descripcion(dto.getDescripcion())
                .diagnostico(dto.getDiagnostico()).tratamiento(dto.getTratamiento())
                .medicamentosRecetados(dto.getMedicamentosRecetados())
                .proximaVisita(dto.getProximaVisita()).nombreQuienTrajo(dto.getNombreQuienTrajo()).build();
    }
}