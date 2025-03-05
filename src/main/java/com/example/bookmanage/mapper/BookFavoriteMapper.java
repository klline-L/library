package com.example.bookmanage.mapper;

import com.example.bookmanage.entity.BookFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookFavoriteMapper {
    void insert(BookFavorite favorite);
    List<BookFavorite> findByUserId(@Param("userId") Long userId);
    void deleteByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);
}