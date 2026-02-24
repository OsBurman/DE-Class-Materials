package com.academy.products.controller;

import com.academy.products.dto.ProductRequestDto;
import com.academy.products.dto.ProductResponseDto;
import com.academy.products.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST Controller for product management.
 *
 * TODO Task 6: Implement all endpoint methods.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/products — optional ?category= query param
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(
            @RequestParam(required = false) String category) {
        // TODO: return 200 with list
        return ResponseEntity.ok(productService.getAllProducts(category));
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        // TODO: return 200 with product; ProductNotFoundException → 404 (handled by GlobalExceptionHandler)
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // POST /api/products — 201 Created with Location header
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto dto) {
        // TODO: create product
        // TODO: build Location header: /api/products/{newId}
        // TODO: return ResponseEntity.created(location).body(created)
        return ResponseEntity.ok().build();
    }

    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto dto) {
        // TODO
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    // PATCH /api/products/{id}/stock?quantity=50
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        // TODO
        return ResponseEntity.ok().build();
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // TODO
        return ResponseEntity.noContent().build();
    }
}
