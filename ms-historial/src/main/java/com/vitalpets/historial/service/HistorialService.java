package com.vitalpets.historial.service;

import com.vitalpets.historial.dto.HistorialDto;
import com.vitalpets.historial.model.HistorialMedico;
import com.vitalpets.historial.model.TipoEvento;
import com.vitalpets.historial.repository.HistorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistorialService {

    private final HistorialRepository historialRepository;

    public HistorialDto registrar(HistorialDto dto) {
        log.info("Registrando evento médico para mascota ID: {} - Tipo: {}",
                dto.getMascotaId(), dto.getTipoEvento());
        HistorialDto resultado = toDto(historialRepository.save(toEntity(dto)));
        log.info("Evento médico registrado exitosamente con ID: {}", resultado.getId());
        return resultado;
    }

    public List<HistorialDto> listarTodos() {
        log.info("Consultando historial médico completo");
        return historialRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<HistorialDto> porMascota(Long mascotaId) {
        log.info("Consultando historial de mascota ID: {}", mascotaId);
        return historialRepository.findByMascotaIdOrderByFechaEventoDesc(mascotaId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<HistorialDto> porCliente(Long clienteId) {
        log.info("Consultando historial del cliente ID: {}", clienteId);
        return historialRepository.findByClienteId(clienteId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public HistorialDto buscarPorId(Long id) {
        log.info("Buscando registro de historial con ID: {}", id);
        return toDto(historialRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Registro de historial no encontrado con ID: {}", id);
                    return new RuntimeException("Registro no encontrado: " + id);
                }));
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