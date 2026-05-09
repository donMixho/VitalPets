package com.vitalpets.personal.service;

import com.vitalpets.personal.dto.PersonalDto;
import com.vitalpets.personal.model.Especialidad;
import com.vitalpets.personal.model.Personal;
import com.vitalpets.personal.repository.PersonalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalService {

    private final PersonalRepository personalRepository;

    public PersonalDto registrar(PersonalDto dto) {
        return toDto(personalRepository.save(toEntity(dto)));
    }

    public List<PersonalDto> listarActivos() {
        return personalRepository.findByActivoTrue()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public PersonalDto buscarPorId(Long id) {
        return toDto(personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado: " + id)));
    }

    public List<PersonalDto> buscarPorEspecialidad(Especialidad especialidad) {
        return personalRepository.findByEspecialidad(especialidad)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public PersonalDto actualizarImplementos(Long id, String implementos) {
        Personal p = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado: " + id));
        p.setImplementosAsignados(implementos);
        return toDto(personalRepository.save(p));
    }

    public void desactivar(Long id) {
        Personal p = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado: " + id));
        p.setActivo(false);
        personalRepository.save(p);
    }

    private PersonalDto toDto(Personal p) {
        return PersonalDto.builder()
                .id(p.getId()).nombre(p.getNombre()).apellido(p.getApellido())
                .rut(p.getRut()).telefono(p.getTelefono()).email(p.getEmail())
                .profesion(p.getProfesion()).especialidad(p.getEspecialidad())
                .implementosAsignados(p.getImplementosAsignados())
                .activo(p.getActivo()).build();
    }

    private Personal toEntity(PersonalDto dto) {
        return Personal.builder()
                .nombre(dto.getNombre()).apellido(dto.getApellido())
                .rut(dto.getRut()).telefono(dto.getTelefono()).email(dto.getEmail())
                .profesion(dto.getProfesion()).especialidad(dto.getEspecialidad())
                .implementosAsignados(dto.getImplementosAsignados()).build();
    }
}