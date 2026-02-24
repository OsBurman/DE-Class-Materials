package com.library;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanLifecycleApp {

    public static void main(String[] args) {
        // TODO: Create an AnnotationConfigApplicationContext with LibraryConfig.class
        //       This starts the Spring container; @PostConstruct callbacks will fire here


        // TODO: Retrieve the CatalogService bean from the context


        // TODO: Call catalogService.search("spring") and print the result
        //       Format: "[CatalogService] search result: " + result


        // TODO: Close the context — this triggers @PreDestroy callbacks
    }
}
