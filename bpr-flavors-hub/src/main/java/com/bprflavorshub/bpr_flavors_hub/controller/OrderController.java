package com.bprflavorshub.bpr_flavors_hub.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.OrderResponse;
import com.bprflavorshub.bpr_flavors_hub.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================================================
    // CUSTOMER - MY ORDERS
    // =========================================================

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Principal principal) {

        return ResponseEntity.ok(
                orderService.getMyOrders(
                        principal.getName()
                )
        );
    }

    // =========================================================
    // CUSTOMER - CANCEL OWN ORDER
    // =========================================================

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelMyOrder(
            Principal principal,
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.cancelMyOrder(
                        principal.getName(),
                        id
                )
        );
    }

    // =========================================================
    // ADMIN - ALL ORDERS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        id,
                        status
                )
        );
    }

    // =========================================================
    // DELETE ORDER - ADMIN
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(
            @PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity.ok(
                "Order deleted successfully."
        );
    }
}