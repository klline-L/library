package com.example.bookmanage.mapper;

import com.example.bookmanage.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BookMapper {
    List<Book> findAll();
    void insert(Book book);
    void update(Book book);
    void delete(@Param("id") Long id);
    Book findById(@Param("id") Long id);
    List<Book> findBooksByPage(@Param("offset") int offset, @Param("limit") int limit);
    void incrementViewCount(@Param("bookId") Long bookId);
    void updateStatus(@Param("bookId") Long bookId, @Param("status") String status);
    List<Book> searchBooks(
            @Param("title") String title,
            @Param("author") String author,
            @Param("categoryId") Long categoryId, // 修改为 Long 类型，与 category_id 一致
            @Param("limit") int limit,
            @Param("offset") int offset
    );
    List<Book> getBooksByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit, @Param("offset") int offset);
    List<Book> findBooksByPageOrderByAddedDate(@Param("offset") int offset, @Param("limit") int limit);
    List<Book> findBooksByPageOrderByLikeCount(@Param("offset") int offset, @Param("limit") int limit);
    List<Book> findTopByOrderByAddedDate(@Param("limit") int limit);
    List<Book> findTopByOrderByLikeCount(@Param("limit") int limit);
}