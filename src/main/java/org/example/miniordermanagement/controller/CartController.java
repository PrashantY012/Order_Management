package org.example.miniordermanagement.controller;
import org.example.miniordermanagement.Service.CartService;
import org.example.miniordermanagement.dto.CartItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Simulated auth (replace with JWT/SecurityContext)
    private String getUserId() {
        return "123";
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(@RequestBody CartItem request) {
        if (request.getQuantity() <= 0 || request.getProductId() == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        cartService.addItem(
                request.getUserId(),
                request.getProductId(),
                request.getQuantity()
        );
        return ResponseEntity.ok("Item added to cart");
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<?> updateItem(
            @PathVariable String productId,
            @RequestBody CartItem request
    ) {
        cartService.updateItem(
                getUserId(),
                productId,
                request.getQuantity()
        );
        return ResponseEntity.ok("Cart updated");
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<?> removeItem(@PathVariable String productId) {
        cartService.removeItem(getUserId(), productId);
        return ResponseEntity.ok("Item removed");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart() {
        cartService.clearCart(getUserId());
        return ResponseEntity.ok("Cart cleared");
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout() {
        if (cartService.isCartEmpty(getUserId())) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        // 1. Fetch cart
        // 2. Validate inventory & pricing
        // 3. Create order in DB (transaction)
        // 4. Deduct inventory

        cartService.clearCart(getUserId());
        return ResponseEntity.ok("Order placed successfully");
    }
}

