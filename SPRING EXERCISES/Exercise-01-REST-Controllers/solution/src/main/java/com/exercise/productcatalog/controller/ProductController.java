package com.exercise.productcatalog.controller;

import com.exercise.productcatalog.model.Product;
import com.exercise.productcatalog.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // SOLUTION TODO 1
@RequestMapping("/api/products") // SOLUTION TODO 1
public class ProductController {

    private final ProductRepository productRepository;

    // SOLUTION TODO 2 — constructor injection, no @Autowired needed (single
    // constructor)
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // SOLUTION TODO 3
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) Optional<String> category) {

        List<Product> products = category
                .map(productRepository::findByCategory)
                .orElse(productRepository.findAll());

        return ResponseEntity.ok(products);
    }

    // SOLUTION TODO 4
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // SOLUTION TODO 5
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // SOLUTION TODO 6
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updated) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setDescription(updated.getDescription());
                    existing.setPrice(updated.getPrice());
                    existing.setCategory(updated.getCategory());
                    existing.setStock(updated.getStock());
                    return ResponseEntity.ok(productRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // SOLUTION TODO 7
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productRepository.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // SOLUTION TODO 8 — must be declared BEFORE /{id} in the class to avoid
    // ambiguity
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String name) {
        List<Product> results = productRepository.findByNameContainingIgnoreCase(name);
        return ResponseEntity.ok(results);
    }
}
