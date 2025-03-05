package com.example.bookmanage.service;

import com.example.bookmanage.dto.BorrowedBookDTO;
import com.example.bookmanage.entity.Book;
import com.example.bookmanage.entity.BookBorrow;
import com.example.bookmanage.entity.BookFavorite;
import com.example.bookmanage.entity.BookInteraction;
import com.example.bookmanage.entity.Comment;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BookService {
    // 获取所有图书
    List<Book> getAllBooks();

    // 添加图书
    void addBook(Book book);

    // 更新图书
    void updateBook(Book book);

    // 删除图书
    void deleteBook(Long id);

    // 根据 ID 获取图书
    Book getBookById(Long id);

    // 分页获取图书
    List<Book> getBooksByPage(int page, int size, String sort);

    // 获取推荐图书
    List<Book> getRecommendedBooks();

    // 增加查看次数
    void incrementViewCount(Long bookId);

    // 获取点赞数
    int getLikeCount(Long bookId);

    // 获取点踩数
    int getDislikeCount(Long bookId);

    // 检查用户是否点赞
    boolean hasLiked(Long userId, Long bookId);

    // 检查用户是否点踩
    boolean hasDisliked(Long userId, Long bookId);

    // 切换点赞状态
    void toggleLike(Long userId, Long bookId);

    // 切换点踩状态
    void toggleDislike(Long userId, Long bookId);

    // 添加评论
    void addComment(Comment comment);

    // 获取图书评论
    List<Comment> getCommentsByBookId(Long bookId);

    // 借阅图书
    void borrowBook(Long bookId, Long userId);

    // 归还图书
    void returnBook(Long borrowId, Long userId);

    // 获取用户借阅图书
    List<BorrowedBookDTO> getBorrowedBooksByUserId(Long userId);

    // 搜索图书
    List<Book> searchBooks(String title, String author, Long categoryId, Pageable pageable);

    // 按分类获取图书
    List<Book> getBooksByCategory(Long categoryId, Pageable pageable);

    // 添加收藏
    void addFavoriteBook(Long userId, Long bookId);

    // 移除收藏
    void removeFavoriteBook(Long userId, Long bookId);

    // 获取用户收藏图书
    List<Book> getFavoriteBooksByUserId(Long userId);

    // 检查图书是否被收藏
    boolean isBookFavorited(Long userId, Long bookId);

    // 获取最新图书
    List<Book> getTopNewBooks(int limit);

    // 获取热门图书
    List<Book> getTopPopularBooks(int limit);

    // 新增：批量删除图书
    void deleteBooks(List<Long> bookIds);

    // 新增：批量更新图书
    void updateBooks(List<Book> books);
}