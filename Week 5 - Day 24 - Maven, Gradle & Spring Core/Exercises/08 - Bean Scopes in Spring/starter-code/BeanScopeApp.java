package com.library;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanScopeApp {

    public static void main(String[] args) {
        // TODO: Create the Spring context using ScopeConfig.class


        System.out.println("=== Singleton Scope ===");
        // TODO: Retrieve CartService twice into two separate variables: cart1 and cart2
        //       Both calls should return the SAME object

        // TODO: Call cart1.addItem("Book A")

        // TODO: Print: "cart2.getItems() → " + cart2.getItems()
        //       Even though you added the item via cart1, cart2 should show it too


        System.out.println();
        System.out.println("=== Prototype Scope ===");
        // TODO: Retrieve RequestContext twice into two separate variables: rc1 and rc2
        //       Each call should return a DIFFERENT object with a DIFFERENT requestId

        // TODO: Print: "requestContext1 ID: " + rc1.getRequestId()
        // TODO: Print: "requestContext2 ID: " + rc2.getRequestId()
        //       The two IDs must be different values


        // TODO: Close the context
    }
}
