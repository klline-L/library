package com.example.bookmanage.mapper;

import com.example.bookmanage.entity.BookInteraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookInteractionMapper {
    void insert(BookInteraction interaction);
    void deleteInteraction(@Param("userId") Long userId, @Param("bookId") Long bookId);
    int countInteractions(@Param("bookId") Long bookId, @Param("type") String type);
    boolean hasInteraction(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("type") String type);
}