package com.vitalpets.vacunas.service;

import com.vitalpets.vacunas.dto.VacunaDto;
import com.vitalpets.vacunas.model.Vacuna;
import com.vitalpets.vacunas.repository.VacunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacunaService {

    private final VacunaRepository vacunaRepository;

    public VacunaDto registrar(VacunaDto dto) {
        return toDto(vacunaRepository.save(toEntity(dto)));
    }

    public List<VacunaDto> listarVigentes() {
        return vacunaRepository.findByVigenteTrue()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<VacunaDto> porMascota(Long mascotaId) {
        return vacunaRepository.findByMascotaId(mascotaId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public VacunaDto buscarPorId(Long id) {
        return toDto(vacunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada: " + id)));
    }

    // Vacunas que vencen en los próximos 30 días
    public List<VacunaDto> proximasAVencer() {
        return vacunaRepository.findByFechaProximaDosisBeforeAndVigenteTrue(
                LocalDate.now().plusDays(30))
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private VacunaDto toDto(Vacuna v) {
        return VacunaDto.builder()
                .id(v.getId()).mascotaId(v.getMascotaId()).clienteId(v.getClienteId())
                .personalId(v.getPersonalId()).nombreVacuna(v.getNombreVacuna())
                .laboratorio(v.getLaboratorio()).lote(v.getLote())
                .fechaAplicacion(v.getFechaAplicacion()).fechaProximaDosis(v.getFechaProximaDosis())
                .dosis(v.getDosis()).observaciones(v.getObservaciones())
                .vigente(v.getVigente()).build();
    }

    private Vacuna toEntity(VacunaDto dto) {
        return Vacuna.builder()
                .mascotaId(dto.getMascotaId()).clienteId(dto.getClienteId())
                .personalId(dto.getPersonalId()).nombreVacuna(dto.getNombreVacuna())
                .laboratorio(dto.getLaboratorio()).lote(dto.getLote())
                .fechaAplicacion(dto.getFechaAplicacion()).fechaProximaDosis(dto.getFechaProximaDosis())
                .dosis(dto.getDosis()).observaciones(dto.getObservaciones()).build();
    }
}