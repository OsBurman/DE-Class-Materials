package com.exercise.productcatalog.controller;

import com.exercise.productcatalog.model.Product;
import com.exercise.productcatalog.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// TODO 1: Add the @RestController annotation to mark this class as a REST controller.
//         Add @RequestMapping("/api/products") to set the base path for all endpoints.
public class ProductController {

    private final ProductRepository productRepository;

    // TODO 2: Add @Autowired OR simply rely on Spring's constructor injection.
    //         Spring automatically injects the ProductRepository bean here because
    //         there is only one constructor. No annotation needed for single-constructor injection!
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // TODO 3: Map this method to GET /api/products
    //         - If `category` is present, return products filtered by category
    //         - Otherwise, return ALL products
    //         - Return HTTP 200 OK
    //         Hint: Use @GetMapping and add @RequestParam(required = false) Optional<String> category
    public ResponseEntity<List<Product>> getAllProducts(Optional<String> category) {
        // your code here
        return null;
    }

    // TODO 4: Map this method to GET /api/products/{id}
    //         - Look up the product by id
    //         - Return 200 OK with the product if found
    //         - Return 404 Not Found if not found
    //         Hint: productRepository.findById(id) returns Optional<Product>
    public ResponseEntity<Product> getProductById(Long id) {
        // your code here
        return null;
    }

    // TODO 5: Map this method to POST /api/products
    //         - Accept a Product in the request body (JSON → Product object)
    //         - Save and return the product
    //         - Return HTTP 201 Created
    //         Hint: Use ResponseEntity.status(HttpStatus.CREATED).body(saved)
    //         OR    ResponseEntity.created(URI.create("/api/products/" + saved.getId())).body(saved)
    public ResponseEntity<Product> createProduct(Product product) {
        // your code here
        return null;
    }

    // TODO 6: Map this method to PUT /api/products/{id}
    //         - If the product exists: update all fields from `updated`, save, return 200 OK
    //         - If not found: return 404 Not Found
    //         Hint: Copy fields from `updated` into the existing product, then save
    public ResponseEntity<Product> updateProduct(Long id, Product updated) {
        // your code here
        return null;
    }

    // TODO 7: Map this method to DELETE /api/products/{id}
    //         - If the product exists: delete it, return 204 No Content
    //         - If not found: return 404 Not Found
    //         Hint: ResponseEntity.noContent().build() for 204
    public ResponseEntity<Void> deleteProduct(Long id) {
        // your code here
        return null;
    }

    // TODO 8: Map this method to GET /api/products/search
    //         - Accept a required @RequestParam named "name"
    //         - Return all products whose name contains the keyword (case-insensitive)
    //         - Return 200 OK with the list (may be empty)
    //         IMPORTANT: This method must be mapped BEFORE /{id} or Spring may confuse
    //         "search" for an ID — use @GetMapping("/search") explicitly.
    public ResponseEntity<List<Product>> searchByName(String name) {
        // your code here
        return null;
    }
}
