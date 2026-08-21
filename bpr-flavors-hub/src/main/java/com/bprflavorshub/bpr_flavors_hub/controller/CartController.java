package com.bprflavorshub.bpr_flavors_hub.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.CartRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.CartResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.CheckoutRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.CheckoutResponse;
import com.bprflavorshub.bpr_flavors_hub.service.CartService;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // =========================================================
    // ADD ITEM TO CART
    // =========================================================

    @PostMapping
    public ResponseEntity<?> addToCart(
            Principal principal,
            @RequestBody CartRequest request) {

        System.out.println();
        System.out.println("========== ADD TO CART ==========");

        printPrincipal(principal);

        if (request != null) {

            System.out.println(
                    "Food ID  : "
                            + request.getFoodId()
            );

            System.out.println(
                    "Quantity : "
                            + request.getQuantity()
            );

        } else {

            System.out.println(
                    "ERROR: CartRequest is NULL"
            );
        }

        System.out.println(
                "=================================");
        System.out.println();

        if (principal == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "User is not authenticated. JWT token is missing or invalid."
                    );
        }

        try {

            return ResponseEntity.ok(
                    cartService.addToCart(
                            principal.getName(),
                            request
                    )
            );

        } catch (RuntimeException e) {

            printApplicationError(
                    "ADD TO CART",
                    e
            );

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // GET CART
    // =========================================================

    @GetMapping
    public ResponseEntity<?> getCart(
            Principal principal) {

        System.out.println();
        System.out.println("========== GET CART ==========");

        printPrincipal(principal);

        System.out.println(
                "=============================="
        );
        System.out.println();

        if (principal == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "User is not authenticated. JWT token is missing or invalid."
                    );
        }

        try {

            List<CartResponse> cart =
                    cartService.getCart(
                            principal.getName()
                    );

            System.out.println(
                    "CART ITEMS : "
                            + cart.size()
            );

            return ResponseEntity.ok(cart);

        } catch (RuntimeException e) {

            printApplicationError(
                    "GET CART",
                    e
            );

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // UPDATE QUANTITY
    // =========================================================

    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateQuantity(
            Principal principal,
            @PathVariable Long cartId,
            @RequestParam Integer quantity) {

        System.out.println();
        System.out.println(
                "========== UPDATE CART =========="
        );

        printPrincipal(principal);

        System.out.println(
                "Cart ID  : " + cartId
        );

        System.out.println(
                "Quantity : " + quantity
        );

        System.out.println(
                "================================="
        );
        System.out.println();

        if (principal == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "User is not authenticated."
                    );
        }

        try {

            return ResponseEntity.ok(
                    cartService.updateQuantity(
                            principal.getName(),
                            cartId,
                            quantity
                    )
            );

        } catch (RuntimeException e) {

            printApplicationError(
                    "UPDATE CART",
                    e
            );

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // REMOVE ITEM
    // =========================================================

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> removeItem(
            Principal principal,
            @PathVariable Long cartId) {

        System.out.println();
        System.out.println(
                "========== REMOVE CART ITEM =========="
        );

        printPrincipal(principal);

        System.out.println(
                "Cart ID : " + cartId
        );

        System.out.println(
                "======================================"
        );
        System.out.println();

        if (principal == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "User is not authenticated."
                    );
        }

        try {

            cartService.removeItem(
                    principal.getName(),
                    cartId
            );

            return ResponseEntity.ok(
                    "Item removed successfully."
            );

        } catch (RuntimeException e) {

            printApplicationError(
                    "REMOVE CART ITEM",
                    e
            );

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // CLEAR CART
    // =========================================================

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(
            Principal principal) {

        System.out.println();
        System.out.println(
                "========== CLEAR CART =========="
        );

        printPrincipal(principal);

        System.out.println(
                "================================"
        );
        System.out.println();

        if (principal == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "User is not authenticated."
                    );
        }

        try {

            cartService.clearCart(
                    principal.getName()
            );

            return ResponseEntity.ok(
                    "Cart cleared successfully."
            );

        } catch (RuntimeException e) {

            printApplicationError(
                    "CLEAR CART",
                    e
            );

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // CHECKOUT
    // =========================================================

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            Principal principal,
            @RequestBody CheckoutRequest request) {

        System.out.println();
        System.out.println(
                "================================================="
        );

        System.out.println(
                "              CHECKOUT REQUEST"
        );

        System.out.println(
                "================================================="
        );

        System.out.println(
                "HTTP Method : POST"
        );

        System.out.println(
                "Endpoint    : /api/cart/checkout"
        );

        // =====================================================
        // PRINCIPAL
        // =====================================================

        if (principal == null) {

            System.out.println(
                    "PRINCIPAL   : NULL"
            );

            System.out.println(
                    "RESULT      : USER NOT AUTHENTICATED"
            );

            System.out.println(
                    "================================================="
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "User is not authenticated. JWT token is missing, invalid or expired."
                    );
        }

        System.out.println(
                "PRINCIPAL   : " + principal
        );

        System.out.println(
                "PHONE       : " + principal.getName()
        );

        // =====================================================
        // REQUEST
        // =====================================================

        if (request == null) {

            System.out.println(
                    "REQUEST     : NULL"
            );

            System.out.println(
                    "RESULT      : CHECKOUT REQUEST IS NULL"
            );

            System.out.println(
                    "================================================="
            );

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Checkout request is required."
                    );
        }

        System.out.println(
                "ADDRESS     : "
                        + request.getDeliveryAddress()
        );

        System.out.println(
                "PAYMENT     : "
                        + request.getPaymentMethod()
        );

        System.out.println(
                "CONTROLLER  : Request reached CartController"
        );

        System.out.println(
                "SECURITY    : Request passed SecurityConfig"
        );

        // =====================================================
        // SERVICE CALL
        // =====================================================

        try {

            System.out.println(
                    "SERVICE     : Calling CartService.checkout()"
            );

            CheckoutResponse response =
                    cartService.checkout(
                            principal.getName(),
                            request
                    );

            System.out.println(
                    "CHECKOUT    : SUCCESS"
            );

            System.out.println(
                    "ORDER IDS   : "
                            + response.getOrderIds()
            );

            System.out.println(
                    "TOTAL ITEMS : "
                            + response.getTotalItems()
            );

            System.out.println(
                    "TOTAL AMOUNT: "
                            + response.getTotalAmount()
            );

            System.out.println(
                    "================================================="
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            System.out.println();
            System.out.println(
                    "**************** CHECKOUT ERROR ****************"
            );

            System.out.println(
                    "Exception : "
                            + e.getClass().getName()
            );

            System.out.println(
                    "Message   : "
                            + e.getMessage()
            );

            System.out.println(
                    "*************************************************"
            );
            System.out.println();

            // -------------------------------------------------
            // CART EMPTY
            // -------------------------------------------------

            if ("Cart is empty".equalsIgnoreCase(
                    e.getMessage())) {

                System.out.println(
                        "CHECKOUT RESULT : CART IS EMPTY"
                );

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(
                                "Cart is empty. Please add food items before checkout."
                        );
            }

            // -------------------------------------------------
            // OTHER CHECKOUT ERRORS
            // -------------------------------------------------

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            e.getMessage()
                    );
        }
    }

    // =========================================================
    // PRINCIPAL DEBUG
    // =========================================================

    private void printPrincipal(
            Principal principal) {

        if (principal == null) {

            System.out.println(
                    "PRINCIPAL : NULL"
            );

            System.out.println(
                    "AUTH      : USER IS NOT AUTHENTICATED"
            );

            return;
        }

        System.out.println(
                "PRINCIPAL : "
                        + principal
        );

        System.out.println(
                "USERNAME  : "
                        + principal.getName()
        );

        System.out.println(
                "AUTH      : USER IS AUTHENTICATED"
        );
    }

    // =========================================================
    // APPLICATION ERROR DEBUG
    // =========================================================

    private void printApplicationError(
            String operation,
            RuntimeException e) {

        System.out.println();
        System.out.println(
                "**************** APPLICATION ERROR ****************"
        );

        System.out.println(
                "Operation : " + operation
        );

        System.out.println(
                "Exception : "
                        + e.getClass().getName()
        );

        System.out.println(
                "Message   : "
                        + e.getMessage()
        );

        System.out.println(
                "********************************************************"
        );
        System.out.println();
    }
}