package com.exercise.productapi.integration;

import com.exercise.productapi.dto.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO 16: Add two annotations:
//          @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//            — loads the FULL Spring application context, starts a real server on a random port
//          @AutoConfigureMockMvc
//            — auto-configures MockMvc to work with the full application context
//
//          This is an INTEGRATION test — it tests the full stack:
//          Controller → Service → Repository → H2 database
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // TODO 17: Implement a full create-then-retrieve integration test.
    // Step 1: Create a product via POST /api/products
    // Step 2: Extract the ID from the response
    // Step 3: GET /api/products/{id} and verify the product name matches
    //
    // Hint: You can chain assertions or use andReturn() to extract response data:
    // String responseBody =
    // mockMvc.perform(...).andReturn().getResponse().getContentAsString()
    // Then parse it: objectMapper.readTree(responseBody).get("id").asLong()
    @Test
    void createProduct_thenRetrieveById_returnsCorrectProduct() throws Exception {
        // TODO 17: implement this full integration test
    }
}
