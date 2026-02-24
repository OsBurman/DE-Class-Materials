package com.library.model;

// DTO used for create/update requests — does NOT include id
// The server is responsible for assigning the id
public record BookDto(String title, String genre) {}
