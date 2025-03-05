package com.example.bookmanage.dto;

import lombok.Data;

@Data
public class SearchForm {
    private String title;
    private String author;
    private String category;
    private Long categoryId;
}