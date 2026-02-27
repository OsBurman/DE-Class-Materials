package com.exercise.productcatalog.model;

// A simple POJO (Plain Old Java Object) representing a product.
// No database yet — we'll store products in an in-memory list.
public class Product {

    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private int stock;

    // ─── Constructors ────────────────────────────────────────────────────────

    public Product() {
    }

    public Product(Long id, String name, String description, double price, String category, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stock = stock;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
