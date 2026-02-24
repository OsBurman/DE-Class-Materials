package com.academy.products.service;

import com.academy.products.dto.ProductRequestDto;
import com.academy.products.dto.ProductResponseDto;

import java.util.List;

/**
 * Contract for the product service layer.
 * This interface is COMPLETE — implement it in ProductServiceImpl.
 */
public interface ProductService {

    List<ProductResponseDto> getAllProducts(String category);

    ProductResponseDto getProductById(Long id);

    ProductResponseDto createProduct(ProductRequestDto dto);

    ProductResponseDto updateProduct(Long id, ProductRequestDto dto);

    void updateStock(Long id, int quantity);

    void deleteProduct(Long id);
}
