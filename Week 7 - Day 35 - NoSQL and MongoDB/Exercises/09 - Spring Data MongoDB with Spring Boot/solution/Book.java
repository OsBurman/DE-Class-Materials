package com.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// @Document maps this POJO to the "books" MongoDB collection
@Document(collection = "books")
public class Book {

    @Id  // MongoDB uses _id as the primary key; Spring Data maps it to this field
    private String id;

    private String title;
    private String author;
    private String genre;
    private int    year;
    private double price;

    // All-args constructor — used when creating new Book objects
    public Book(String id, String title, String author, String genre, int year, double price) {
        this.id     = id;
        this.title  = title;
        this.author = author;
        this.genre  = genre;
        this.year   = year;
        this.price  = price;
    }

    // No-arg constructor — required by Spring Data MongoDB for deserialization
    public Book() {}

    // Getters — Spring Data reads fields via getters when serializing/deserializing
    public String getId()     { return id; }
    public String getTitle()  { return title; }
    public String getAuthor() { return author; }
    public String getGenre()  { return genre; }
    public int    getYear()   { return year; }
    public double getPrice()  { return price; }
}
