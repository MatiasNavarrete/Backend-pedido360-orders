package com.example.pedidos360_orders.controller;

import com.example.pedidos360_orders.client.CatalogoClient;
import com.example.pedidos360_orders.entity.Pedido;
import com.example.pedidos360_orders.enums.EstadoPedido;
import com.example.pedidos360_orders.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private CatalogoClient catalogoClient;

    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        pedido.setEstado(EstadoPedido.CREADO);
        return pedidoRepository.save(pedido);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam EstadoPedido nuevoEstado) {
        return pedidoRepository.findById(id).map(pedido -> {

            if (nuevoEstado == EstadoPedido.DESPACHADO &&
                    (pedido.getEstado() == EstadoPedido.CREADO || pedido.getEstado() == EstadoPedido.CANCELADO)) {
                return ResponseEntity.badRequest().body("Error: El pedido debe ser ACEPTADO antes de ser DESPACHADO.");
            }

            if (nuevoEstado == EstadoPedido.ACEPTADO && pedido.getEstado() != EstadoPedido.ACEPTADO) {
                try {
                    catalogoClient.reducirStock(pedido.getProductoId(), pedido.getCantidad());
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Error: Stock insuficiente o producto no encontrado.");
                }
            }

            pedido.setEstado(nuevoEstado);
            pedidoRepository.save(pedido);
            return ResponseEntity.ok().body("Estado actualizado a: " + nuevoEstado);

        }).orElse(ResponseEntity.notFound().build());
    }
}