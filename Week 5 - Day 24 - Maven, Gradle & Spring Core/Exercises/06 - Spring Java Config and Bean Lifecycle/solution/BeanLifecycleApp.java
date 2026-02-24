package com.library;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanLifecycleApp {

    public static void main(String[] args) {
        // Starting the context triggers: instantiation → DI → @PostConstruct for each bean
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(LibraryConfig.class);

        CatalogService catalogService = context.getBean(CatalogService.class);

        String result = catalogService.search("spring");
        System.out.println("[CatalogService] search result: " + result);

        // Closing the context triggers @PreDestroy callbacks in reverse registration order
        context.close();
    }
}
