package com.example.bookmanage.entity;

import jdk.jfr.Category;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Double price;
    private String coverImageUrl; // 封面图片 URL
    private int viewCount; // 查看次数
    private String status; //书籍状态
    private Category category;//类别字段
    private LocalDateTime addedDate;
    private Integer likeCount;
    private Long categoryId;

}