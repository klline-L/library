package com.example.bookmanage.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookFavorite {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDate addedDate;
}