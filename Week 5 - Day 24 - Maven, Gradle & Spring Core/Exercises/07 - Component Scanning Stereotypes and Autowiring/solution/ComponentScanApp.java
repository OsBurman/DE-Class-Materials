package com.library;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ComponentScanApp {

    public static void main(String[] args) {
        // Component scanning fires during context creation
        // Spring finds @Repository, @Service, @Component and registers them automatically
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ScanningConfig.class);

        AuthorController controller = context.getBean(AuthorController.class);
        System.out.println(controller.handleRequest(7));

        context.close();
    }
}
