package com.example.bookmanage.entity;

import lombok.Data;

@Data
public class BookInteraction {
    private Long id;
    private Long userId;
    private Long bookId;
    private String type; // 赞 或 踩
}