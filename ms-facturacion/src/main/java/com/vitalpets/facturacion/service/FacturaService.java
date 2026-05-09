package com.vitalpets.facturacion.service;

import com.vitalpets.facturacion.dto.DetalleFacturaDto;
import com.vitalpets.facturacion.dto.FacturaDto;
import com.vitalpets.facturacion.model.*;
import com.vitalpets.facturacion.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaDto crear(FacturaDto dto) {
        Factura factura = Factura.builder()
                .citaId(dto.getCitaId()).clienteId(dto.getClienteId())
                .mascotaId(dto.getMascotaId()).personalId(dto.getPersonalId())
                .nombreQuienTrae(dto.getNombreQuienTrae())
                .metodoPago(dto.getMetodoPago() != null ? dto.getMetodoPago() : MetodoPago.PENDIENTE)
                .build();

        // Agregar detalles y calcular totales
        if (dto.getDetalles() != null) {
            for (DetalleFacturaDto d : dto.getDetalles()) {
                DetalleFactura detalle = DetalleFactura.builder()
                        .factura(factura).descripcion(d.getDescripcion())
                        .tipoItem(d.getTipoItem()).cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getCantidad() * d.getPrecioUnitario()).build();
                factura.getDetalles().add(detalle);
            }
        }

        // Calcular totales separados por tipo
        double totalServicios = factura.getDetalles().stream()
                .filter(d -> "SERVICIO".equals(d.getTipoItem()))
                .mapToDouble(DetalleFactura::getSubtotal).sum();

        double totalProductos = factura.getDetalles().stream()
                .filter(d -> "PRODUCTO".equals(d.getTipoItem()))
                .mapToDouble(DetalleFactura::getSubtotal).sum();

        factura.setTotalServicios(totalServicios);
        factura.setTotalProductos(totalProductos);
        factura.setTotalFinal(totalServicios + totalProductos);

        return toDto(facturaRepository.save(factura));
    }

    public List<FacturaDto> listarTodas() {
        return facturaRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public FacturaDto buscarPorId(Long id) {
        return toDto(facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + id)));
    }

    public List<FacturaDto> porCliente(Long clienteId) {
        return facturaRepository.findByClienteId(clienteId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public FacturaDto pagar(Long id, MetodoPago metodo) {
        Factura f = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + id));
        f.setEstado(EstadoFactura.PAGADA);
        f.setMetodoPago(metodo);
        return toDto(facturaRepository.save(f));
    }

    public void anular(Long id) {
        Factura f = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + id));
        f.setEstado(EstadoFactura.ANULADA);
        facturaRepository.save(f);
    }

    private FacturaDto toDto(Factura f) {
        List<DetalleFacturaDto> detalles = f.getDetalles().stream()
                .map(d -> DetalleFacturaDto.builder()
                        .id(d.getId()).descripcion(d.getDescripcion())
                        .tipoItem(d.getTipoItem()).cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario()).subtotal(d.getSubtotal()).build())
                .collect(Collectors.toList());

        return FacturaDto.builder()
                .id(f.getId()).citaId(f.getCitaId()).clienteId(f.getClienteId())
                .mascotaId(f.getMascotaId()).personalId(f.getPersonalId())
                .nombreQuienTrae(f.getNombreQuienTrae()).fechaEmision(f.getFechaEmision())
                .estado(f.getEstado()).metodoPago(f.getMetodoPago())
                .totalServicios(f.getTotalServicios()).totalProductos(f.getTotalProductos())
                .totalFinal(f.getTotalFinal()).detalles(detalles).build();
    }
}
