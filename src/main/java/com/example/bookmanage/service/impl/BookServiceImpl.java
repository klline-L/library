package com.example.bookmanage.service.impl;

import com.example.bookmanage.dto.BorrowedBookDTO;
import com.example.bookmanage.entity.Book;
import com.example.bookmanage.entity.BookBorrow;
import com.example.bookmanage.entity.BookFavorite;
import com.example.bookmanage.entity.BookInteraction;
import com.example.bookmanage.entity.Comment;
import com.example.bookmanage.mapper.BookBorrowMapper;
import com.example.bookmanage.mapper.BookFavoriteMapper;
import com.example.bookmanage.mapper.BookInteractionMapper;
import com.example.bookmanage.mapper.BookMapper;
import com.example.bookmanage.mapper.CommentMapper;
import com.example.bookmanage.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookInteractionMapper bookInteractionMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private BookBorrowMapper bookBorrowMapper;

    @Autowired
    private BookFavoriteMapper bookFavoriteMapper;

    private static final String FALLBACK_IMAGE_URL = "https://via.placeholder.com/200x300?text=Book+Cover";

    @Override
    public List<Book> getAllBooks() {
        // 获取所有图书并设置封面 URL
        List<Book> books = bookMapper.findAll();
        books.forEach(this::setCoverImageUrl);
        logger.info("获取所有图书: 数量={}", books.size());
        return books;
    }

    @Override
    public void addBook(Book book) {
        // 添加新书并初始化字段
        book.setViewCount(0);
        book.setStatus("available");
        book.setAddedDate(LocalDateTime.now());
        book.setLikeCount(0);
        bookMapper.insert(book);
        logger.info("添加图书 ID: {}, 状态: {}", book.getId(), book.getStatus());
    }

    @Override
    public void updateBook(Book book) {
        // 更新图书信息，若状态为空则设为可用
        if (book.getStatus() == null) {
            book.setStatus("available");
        }
        bookMapper.update(book);
    }

    @Override
    public void deleteBook(Long id) {
        // 删除单本图书
        bookMapper.delete(id);
    }

    @Override
    public Book getBookById(Long id) {
        // 根据 ID 获取图书并设置封面 URL
        Book book = bookMapper.findById(id);
        if (book != null) {
            setCoverImageUrl(book);
            logger.info("获取图书 ID: {}, 浏览次数: {}, 状态: {}", id, book.getViewCount(), book.getStatus());
        }
        return book;
    }

    @Override
    public List<Book> getBooksByPage(int page, int size, String sort) {
        // 分页获取图书，支持按时间或热度排序
        int offset = (page - 1) * size;
        List<Book> books;
        if ("new".equals(sort)) {
            books = bookMapper.findBooksByPageOrderByAddedDate(offset, size);
        } else if ("popular".equals(sort)) {
            books = bookMapper.findBooksByPageOrderByLikeCount(offset, size);
        } else {
            books = bookMapper.findBooksByPage(offset, size);
        }
        books.forEach(this::setCoverImageUrl);
        logger.info("分页获取图书: 页码={}, 每页数量={}, 排序={}, 图书数量={}", page, size, sort, books.size());
        return books;
    }

    @Override
    public List<Book> getRecommendedBooks() {
        // 获取推荐图书（前 3 本）
        List<Book> books = bookMapper.findBooksByPage(0, 3);
        books.forEach(this::setCoverImageUrl);
        logger.info("获取推荐图书: 数量={}", books.size());
        return books;
    }

    @Override
    public void incrementViewCount(Long bookId) {
        // 增加图书查看次数
        logger.info("增加图书 ID: {} 的查看次数", bookId);
        bookMapper.incrementViewCount(bookId);
    }

    @Override
    public int getLikeCount(Long bookId) {
        // 获取图书点赞数
        return bookInteractionMapper.countInteractions(bookId, "like");
    }

    @Override
    public int getDislikeCount(Long bookId) {
        // 获取图书点踩数
        return bookInteractionMapper.countInteractions(bookId, "dislike");
    }

    @Override
    public boolean hasLiked(Long userId, Long bookId) {
        // 检查用户是否点赞
        return bookInteractionMapper.hasInteraction(userId, bookId, "like");
    }

    @Override
    public boolean hasDisliked(Long userId, Long bookId) {
        // 检查用户是否点踩
        return bookInteractionMapper.hasInteraction(userId, bookId, "dislike");
    }

    @Override
    public void toggleLike(Long userId, Long bookId) {
        // 切换点赞状态
        logger.info("切换点赞状态: 用户ID={}, 图书ID={}", userId, bookId);
        if (hasLiked(userId, bookId)) {
            bookInteractionMapper.deleteInteraction(userId, bookId);
        } else {
            if (hasDisliked(userId, bookId)) {
                bookInteractionMapper.deleteInteraction(userId, bookId);
            }
            BookInteraction interaction = new BookInteraction();
            interaction.setUserId(userId);
            interaction.setBookId(bookId);
            interaction.setType("like");
            bookInteractionMapper.insert(interaction);
        }
    }

    @Override
    public void toggleDislike(Long userId, Long bookId) {
        // 切换点踩状态
        logger.info("切换点踩状态: 用户ID={}, 图书ID={}", userId, bookId);
        if (hasDisliked(userId, bookId)) {
            bookInteractionMapper.deleteInteraction(userId, bookId);
        } else {
            if (hasLiked(userId, bookId)) {
                bookInteractionMapper.deleteInteraction(userId, bookId);
            }
            BookInteraction interaction = new BookInteraction();
            interaction.setUserId(userId);
            interaction.setBookId(bookId);
            interaction.setType("dislike");
            bookInteractionMapper.insert(interaction);
        }
    }

    @Override
    public void addComment(Comment comment) {
        // 添加评论
        commentMapper.insert(comment);
    }

    @Override
    public List<Comment> getCommentsByBookId(Long bookId) {
        // 获取某本书的所有评论
        return commentMapper.getCommentsByBookId(bookId);
    }

    @Override
    public void borrowBook(Long bookId, Long userId) {
        // 用户借阅图书
        logger.info("尝试借阅图书 ID: {}, 用户 ID: {}", bookId, userId);
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            logger.error("图书 ID: {} 未找到", bookId);
            throw new IllegalStateException("图书未找到。");
        }

        if (book.getStatus() == null) {
            logger.warn("图书 ID: {} 状态为空，设置为 'available'", bookId);
            book.setStatus("available");
            bookMapper.updateStatus(bookId, "available");
            book = bookMapper.findById(bookId);
        }

        if (!"available".equals(book.getStatus())) {
            logger.error("图书 ID: {} 不可借阅，当前状态: {}", bookId, book.getStatus());
            throw new IllegalStateException("图书不可借阅。");
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14);
        BookBorrow borrow = new BookBorrow();
        borrow.setBookId(bookId);
        borrow.setUserId(userId);
        borrow.setBorrowDate(borrowDate);
        borrow.setDueDate(dueDate);

        bookBorrowMapper.insert(borrow);
        bookMapper.updateStatus(bookId, "borrowed");
        logger.info("图书 ID: {} 被用户 ID: {} 成功借阅，状态更新为 'borrowed'", bookId, userId);
    }

    @Override
    public void returnBook(Long borrowId, Long userId) {
        // 用户归还图书
        BookBorrow borrow = bookBorrowMapper.findById(borrowId);
        if (borrow == null || !borrow.getUserId().equals(userId) || borrow.getReturnDate() != null) {
            logger.error("归还请求无效，借阅 ID: {}, 用户 ID: {}", borrowId, userId);
            throw new IllegalStateException("归还请求无效。");
        }
        borrow.setReturnDate(LocalDate.now());
        bookBorrowMapper.updateReturnDate(borrowId, borrow.getReturnDate());
        bookMapper.updateStatus(borrow.getBookId(), "available");
        logger.info("图书 ID: {} 被用户 ID: {} 归还", borrow.getBookId(), userId);
    }

    @Override
    public List<BorrowedBookDTO> getBorrowedBooksByUserId(Long userId) {
        // 获取用户借阅的图书
        List<BookBorrow> borrows = bookBorrowMapper.findByUserId(userId);
        List<BorrowedBookDTO> borrowedBooks = new ArrayList<>();
        for (BookBorrow borrow : borrows) {
            Book book = bookMapper.findById(borrow.getBookId());
            if (book != null) {
                setCoverImageUrl(book);
                BorrowedBookDTO dto = new BorrowedBookDTO();
                dto.setBook(book);
                dto.setBorrow(borrow);
                borrowedBooks.add(dto);
            }
        }
        logger.info("获取用户 {} 的借阅图书: 数量={}", userId, borrowedBooks.size());
        return borrowedBooks;
    }

    @Override
    public List<Book> searchBooks(String title, String author, Long categoryId, Pageable pageable) {
        // 搜索图书
        int offset = pageable != null ? (int) pageable.getOffset() : 0;
        int limit = pageable != null ? pageable.getPageSize() : Integer.MAX_VALUE;
        List<Book> books = bookMapper.searchBooks(title, author, categoryId, limit, offset);
        books.forEach(this::setCoverImageUrl);
        logger.info("搜索图书: 标题={}, 作者={}, 分类ID={}, 结果数量={}", title, author, categoryId, books.size());
        return books;
    }

    @Override
    public List<Book> getBooksByCategory(Long categoryId, Pageable pageable) {
        // 按分类获取图书
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        List<Book> books = bookMapper.getBooksByCategory(categoryId, limit, offset);
        books.forEach(this::setCoverImageUrl);
        logger.info("按分类 ID {} 获取图书: 数量={}", categoryId, books.size());
        return books;
    }

    @Override
    public void addFavoriteBook(Long userId, Long bookId) {
        // 用户添加图书到收藏
        BookFavorite favorite = new BookFavorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favorite.setAddedDate(LocalDate.now());
        bookFavoriteMapper.insert(favorite);
        logger.info("用户 {} 添加图书 {} 到收藏", userId, bookId);
    }

    @Override
    public void removeFavoriteBook(Long userId, Long bookId) {
        // 用户移除收藏
        bookFavoriteMapper.deleteByUserIdAndBookId(userId, bookId);
        logger.info("用户 {} 从收藏移除图书 {}", userId, bookId);
    }

    @Override
    public List<Book> getFavoriteBooksByUserId(Long userId) {
        // 获取用户收藏的图书
        List<BookFavorite> favorites = bookFavoriteMapper.findByUserId(userId);
        List<Book> favoriteBooks = favorites.stream()
                .map(fav -> bookMapper.findById(fav.getBookId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        logger.info("获取用户 {} 的收藏图书: 数量={}", userId, favoriteBooks.size());
        return favoriteBooks;
    }

    @Override
    public boolean isBookFavorited(Long userId, Long bookId) {
        // 检查图书是否被用户收藏
        List<BookFavorite> favorites = bookFavoriteMapper.findByUserId(userId);
        return favorites.stream().anyMatch(fav -> fav.getBookId().equals(bookId));
    }

    @Override
    public List<Book> getTopNewBooks(int limit) {
        // 获取最新图书
        List<Book> books = bookMapper.findTopByOrderByAddedDate(limit);
        books.forEach(this::setCoverImageUrl);
        logger.info("获取最新图书: 数量={}", books.size());
        return books;
    }

    @Override
    public List<Book> getTopPopularBooks(int limit) {
        // 获取热门图书
        List<Book> books = bookMapper.findTopByOrderByLikeCount(limit);
        books.forEach(this::setCoverImageUrl);
        logger.info("获取热门图书: 数量={}", books.size());
        return books;
    }

    @Override
    public void deleteBooks(List<Long> bookIds) {
        // 批量删除图书
        for (Long bookId : bookIds) {
            bookMapper.delete(bookId);
        }
        logger.info("批量删除图书: IDs={}", bookIds);
    }

    @Override
    public void updateBooks(List<Book> books) {
        // 批量更新图书
        for (Book book : books) {
            if (book.getStatus() == null) {
                book.setStatus("available");
            }
            bookMapper.update(book);
        }
        logger.info("批量更新图书: IDs={}", books.stream().map(Book::getId).collect(Collectors.toList()));
    }

    private void setCoverImageUrl(Book book) {
        // 设置图书封面 URL，若为空则使用默认值
        book.setCoverImageUrl(book.getCoverImageUrl() != null && !book.getCoverImageUrl().isEmpty() ? book.getCoverImageUrl() : FALLBACK_IMAGE_URL);
    }
}