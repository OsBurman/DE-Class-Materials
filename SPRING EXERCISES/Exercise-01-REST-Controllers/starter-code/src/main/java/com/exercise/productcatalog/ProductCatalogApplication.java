package com.exercise.productcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication combines three annotations:
//   @Configuration     — marks this class as a source of bean definitions
//   @EnableAutoConfiguration — tells Spring Boot to auto-configure beans based on classpath
//   @ComponentScan    — scans this package (and sub-packages) for Spring components
@SpringBootApplication
public class ProductCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }
}
