package com.example.bookmanage.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookBorrow {
    private Long id;
    private Long bookId;
    private Long userId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
}