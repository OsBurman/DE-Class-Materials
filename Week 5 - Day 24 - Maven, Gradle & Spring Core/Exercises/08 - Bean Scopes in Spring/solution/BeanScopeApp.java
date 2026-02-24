package com.library;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanScopeApp {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ScopeConfig.class);

        System.out.println("=== Singleton Scope ===");
        // Both variables point to the SAME CartService instance
        CartService cart1 = context.getBean(CartService.class);
        CartService cart2 = context.getBean(CartService.class);

        cart1.addItem("Book A");

        // cart2 sees "Book A" because cart1 == cart2 (same object)
        System.out.println("cart2.getItems() → " + cart2.getItems()
                + "   (same instance — cart1 and cart2 are the same object: "
                + (cart1 == cart2) + ")");

        System.out.println();
        System.out.println("=== Prototype Scope ===");
        // Each getBean() call returns a brand-new RequestContext with its own UUID
        RequestContext rc1 = context.getBean(RequestContext.class);
        RequestContext rc2 = context.getBean(RequestContext.class);

        System.out.println("requestContext1 ID: " + rc1.getRequestId());
        System.out.println("requestContext2 ID: " + rc2.getRequestId()
                + "   (different IDs — two separate instances: " + (rc1 != rc2) + ")");

        context.close();
    }
}
