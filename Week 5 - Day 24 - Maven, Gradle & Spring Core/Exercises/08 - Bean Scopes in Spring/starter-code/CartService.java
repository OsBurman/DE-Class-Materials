package com.library;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// TODO: Add @Component to register this as a Spring bean
// TODO: Add @Scope("singleton") explicitly — singleton is the default, but being explicit here
//       makes the scope visible to students reviewing the code
public class CartService {

    private final List<String> items = new ArrayList<>();

    /**
     * Adds an item to the cart.
     *
     * @param item the item name to add
     */
    public void addItem(String item) {
        // TODO: Add the item to the items list
    }

    /**
     * Returns the cart contents as a formatted string.
     *
     * @return the items list as a string (e.g., "[Book A, Book B]")
     */
    public String getItems() {
        // TODO: Return items.toString()
        return null;
    }
}
