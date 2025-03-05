package com.example.bookmanage.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private Long id;
    private Long userId;
    private Long bookId;
    private String text;
    private Date createdDate;
}