package com.academy.products.exception;

import lombok.Getter;

/**
 * Thrown when a requested product does not exist.
 *
 * TODO Task 7: This exception is declared — you just need to use it in the
 * service
 * and handle it in GlobalExceptionHandler.
 */
@Getter
public class ProductNotFoundException extends RuntimeException {

    private final Long productId;

    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId);
        this.productId = productId;
    }
}
