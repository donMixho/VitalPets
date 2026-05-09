package com.vitalpets.inventario.service;

import com.vitalpets.inventario.dto.ProductoDto;
import com.vitalpets.inventario.model.CategoriaProducto;
import com.vitalpets.inventario.model.Producto;
import com.vitalpets.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoDto registrar(ProductoDto dto) {
        return toDto(productoRepository.save(toEntity(dto)));
    }

    public List<ProductoDto> listarActivos() {
        return productoRepository.findByActivoTrue()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public ProductoDto buscarPorId(Long id) {
        return toDto(productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id)));
    }

    public List<ProductoDto> buscarPorCategoria(CategoriaProducto categoria) {
        return productoRepository.findByCategoria(categoria)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ProductoDto> alertasStockBajo() {
        return productoRepository.findStockBajo()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // Reducir stock al vender un producto al cliente
    public ProductoDto reducirStock(Long id, Integer cantidad) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
        if (p.getStockActual() < cantidad)
            throw new RuntimeException("Stock insuficiente. Disponible: " + p.getStockActual());
        p.setStockActual(p.getStockActual() - cantidad);
        return toDto(productoRepository.save(p));
    }

    // Aumentar stock al recibir mercadería
    public ProductoDto aumentarStock(Long id, Integer cantidad) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
        p.setStockActual(p.getStockActual() + cantidad);
        return toDto(productoRepository.save(p));
    }

    public void desactivar(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
        p.setActivo(false);
        productoRepository.save(p);
    }

    private ProductoDto toDto(Producto p) {
        return ProductoDto.builder()
                .id(p.getId()).nombre(p.getNombre()).descripcion(p.getDescripcion())
                .categoria(p.getCategoria()).stockActual(p.getStockActual())
                .stockMinimo(p.getStockMinimo()).precioUnitario(p.getPrecioUnitario())
                .unidadMedida(p.getUnidadMedida()).activo(p.getActivo())
                .stockBajo(p.getStockActual() <= p.getStockMinimo()).build();
    }

    private Producto toEntity(ProductoDto dto) {
        return Producto.builder()
                .nombre(dto.getNombre()).descripcion(dto.getDescripcion())
                .categoria(dto.getCategoria()).stockActual(dto.getStockActual())
                .stockMinimo(dto.getStockMinimo()).precioUnitario(dto.getPrecioUnitario())
                .unidadMedida(dto.getUnidadMedida()).build();
    }
}