package com.academy.products.service;

import com.academy.products.dto.ProductRequestDto;
import com.academy.products.dto.ProductResponseDto;
import com.academy.products.exception.ProductNotFoundException;
import com.academy.products.model.Product;
import com.academy.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Product business logic. Converts between DTOs and domain model.
 *
 * TODO Task 5: Implement all methods.
 * Remember: throw ProductNotFoundException when a product is not found by ID.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Override
    public List<ProductResponseDto> getAllProducts(String category) {
        // TODO: if category != null, call repository.findByCategory(category)
        // otherwise call repository.findAll()
        // map each Product → ProductResponseDto using toResponseDto()
        return List.of();
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        // TODO: find by id or throw ProductNotFoundException(id)
        // then map to DTO
        return null;
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto dto) {
        // TODO: map dto → Product (id is null), call repository.save(), return DTO
        return null;
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        // TODO: find existing product (throw if not found)
        // update all fields from dto
        // save and return DTO
        return null;
    }

    @Override
    public void updateStock(Long id, int quantity) {
        // TODO: find product, set stockQuantity = quantity, save
    }

    @Override
    public void deleteProduct(Long id) {
        // TODO: check if exists (throw if not), then delete
    }

    // ---- Mapping helpers ----

    private Product toEntity(ProductRequestDto dto) {
        // TODO: return Product.builder()...build()
        return null;
    }

    private ProductResponseDto toResponseDto(Product p) {
        // TODO: return ProductResponseDto.builder()...build()
        // Don't forget formattedPrice!
        return null;
    }
}
