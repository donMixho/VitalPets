package com.vitalpets.clientes.service;

import com.vitalpets.clientes.dto.ClienteDto;
import com.vitalpets.clientes.dto.TerceroDto;
import com.vitalpets.clientes.model.Cliente;
import com.vitalpets.clientes.model.Tercero;
import com.vitalpets.clientes.repository.ClienteRepository;
import com.vitalpets.clientes.repository.TerceroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TerceroRepository terceroRepository;

    // ── CLIENTES ─────────────────────────────────────────────
    public ClienteDto registrar(ClienteDto dto) {
        return toDto(clienteRepository.save(toEntity(dto)));
    }

    public List<ClienteDto> listarActivos() {
        return clienteRepository.findByActivoTrue()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public ClienteDto buscarPorId(Long id) {
        return toDto(clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id)));
    }

    public void desactivar(Long id) {
        Cliente c = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + id));
        c.setActivo(false);
        clienteRepository.save(c);
    }

    // ── TERCEROS ─────────────────────────────────────────────
    public TerceroDto registrarTercero(TerceroDto dto) {
        Tercero t = Tercero.builder()
                .nombre(dto.getNombre()).apellido(dto.getApellido())
                .telefono(dto.getTelefono()).clienteId(dto.getClienteId())
                .relacion(dto.getRelacion()).build();
        return toTerceroDto(terceroRepository.save(t));
    }

    public List<TerceroDto> tercerosPorCliente(Long clienteId) {
        return terceroRepository.findByClienteId(clienteId)
                .stream().map(this::toTerceroDto).collect(Collectors.toList());
    }

    // ── Conversores ───────────────────────────────────────────
    private ClienteDto toDto(Cliente c) {
        return ClienteDto.builder()
                .id(c.getId()).nombre(c.getNombre()).apellido(c.getApellido())
                .tipoDocumento(c.getTipoDocumento()).numeroDocumento(c.getNumeroDocumento())
                .telefono(c.getTelefono()).email(c.getEmail())
                .direccion(c.getDireccion()).activo(c.getActivo()).build();
    }

    private Cliente toEntity(ClienteDto dto) {
        return Cliente.builder()
                .nombre(dto.getNombre()).apellido(dto.getApellido())
                .tipoDocumento(dto.getTipoDocumento()).numeroDocumento(dto.getNumeroDocumento())
                .telefono(dto.getTelefono()).email(dto.getEmail())
                .direccion(dto.getDireccion()).build();
    }

    private TerceroDto toTerceroDto(Tercero t) {
        return TerceroDto.builder()
                .id(t.getId()).nombre(t.getNombre()).apellido(t.getApellido())
                .telefono(t.getTelefono()).clienteId(t.getClienteId())
                .relacion(t.getRelacion()).activo(t.getActivo()).build();
    }
}