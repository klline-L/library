package com.example.bookmanage.dto;

import lombok.Data;

@Data
public class BorrowStatsDTO {
    private String bookTitle;     // 图书标题
    private Long borrowCount;     // 借阅次数
}