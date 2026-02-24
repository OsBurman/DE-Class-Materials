package com.library;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("singleton")  // default; one shared instance per Spring container
public class CartService {

    // This list is shared across ALL references to the same CartService bean
    private final List<String> items = new ArrayList<>();

    public void addItem(String item) {
        items.add(item);
    }

    public String getItems() {
        return items.toString();
    }
}
