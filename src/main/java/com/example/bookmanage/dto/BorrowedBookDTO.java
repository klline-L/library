package com.example.bookmanage.dto;

import com.example.bookmanage.entity.Book;
import com.example.bookmanage.entity.BookBorrow;
import lombok.Data;

@Data
public class BorrowedBookDTO {
    private Book book;
    private BookBorrow borrow;
}