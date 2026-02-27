package com.exercise.productapi.integration;

import com.exercise.productapi.dto.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProduct_thenRetrieveById_returnsCorrectProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Integration Test Laptop");
        request.setDescription("Created in integration test");
        request.setPrice(new BigDecimal("1299.99"));
        request.setCategory("Electronics");
        request.setStock(5);

        // Step 1: Create the product
        MvcResult createResult = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Laptop"))
                .andReturn();

        // Step 2: Extract the ID
        String responseBody = createResult.getResponse().getContentAsString();
        Long id = objectMapper.readTree(responseBody).get("id").asLong();

        // Step 3: Retrieve and verify
        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Laptop"))
                .andExpect(jsonPath("$.category").value("Electronics"));
    }
}
