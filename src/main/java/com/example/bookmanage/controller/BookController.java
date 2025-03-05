package com.example.bookmanage.controller;

import com.example.bookmanage.dto.SearchForm;
import com.example.bookmanage.entity.Book;
import com.example.bookmanage.entity.Comment;
import com.example.bookmanage.entity.User;
import com.example.bookmanage.service.BookService;
import com.example.bookmanage.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    @Autowired
    private SettingsService settingsService;

    // 首页：显示最新和热门图书，以及系统公告和简介
    @GetMapping("/")
    public String home(Model model) {
        List<Book> newBooks = bookService.getTopNewBooks(5);
        List<Book> popularBooks = bookService.getTopPopularBooks(5);
        String announcement = settingsService.getAnnouncement();
        String excerpt = settingsService.getExcerpt();
        model.addAttribute("newBooks", newBooks);
        model.addAttribute("popularBooks", popularBooks);
        model.addAttribute("announcement", announcement);
        model.addAttribute("excerpt", excerpt);
        return "index";
    }

    // 图书列表页面：支持分页和搜索功能，用户可浏览图书
    @GetMapping("/books")
    public String getBooks(@RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           @RequestParam(value = "sort", defaultValue = "") String sort,
                           @ModelAttribute("searchForm") SearchForm searchForm,
                           Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        List<Book> books;
        int totalBooks;

        if (searchForm != null && (searchForm.getTitle() != null || searchForm.getAuthor() != null || searchForm.getCategoryId() != null)) {
            books = bookService.searchBooks(searchForm.getTitle(), searchForm.getAuthor(), searchForm.getCategoryId(), null);
            totalBooks = books.size();
        } else {
            books = bookService.getBooksByPage(page, size, sort);
            totalBooks = bookService.getAllBooks().size();
        }

        int totalPages = (int) Math.ceil((double) totalBooks / size);

        model.addAttribute("books", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasNextPage", page < totalPages && !books.isEmpty());
        model.addAttribute("searchForm", searchForm != null ? searchForm : new SearchForm());
        model.addAttribute("sort", sort);
        logger.info("加载图书列表: 数量={}", books.size());
        return "book-list";
    }

    // 图书详情页面：展示图书详情及用户交互选项
    @GetMapping("/books/{id}")
    public String getBookDetail(@PathVariable("id") Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Book book = bookService.getBookById(id);
        if (book == null) {
            logger.warn("图书 ID {} 未找到", id);
            return "redirect:/books";
        }
        bookService.incrementViewCount(id);
        List<Comment> comments = bookService.getCommentsByBookId(id);
        boolean canBorrow = user != null && "available".equals(book.getStatus());
        boolean isFavorited = user != null && bookService.isBookFavorited(user.getId(), id);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        model.addAttribute("canBorrow", canBorrow);
        model.addAttribute("hasLiked", user != null && bookService.hasLiked(user.getId(), id));
        model.addAttribute("hasDisliked", user != null && bookService.hasDisliked(user.getId(), id));
        model.addAttribute("likeCount", bookService.getLikeCount(id));
        model.addAttribute("dislikeCount", bookService.getDislikeCount(id));
        model.addAttribute("isFavorited", isFavorited);
        logger.info("图书详情: ID={}, 状态={}, 用户={}, 可借阅={}", id, book.getStatus(), user != null ? user.getId() : null, canBorrow);
        return "bookDetail";
    }

    // 借阅图书
    @PostMapping("/books/{id}/borrow")
    public String borrowBook(@PathVariable("id") Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        logger.info("用户 {} 正在借阅图书 ID: {}", user.getId(), id);
        bookService.borrowBook(id, user.getId());
        return "redirect:/books/" + id;
    }

    // 点赞图书
    @PostMapping("/books/{id}/like")
    public String likeBook(@PathVariable("id") Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        bookService.toggleLike(user.getId(), id);
        return "redirect:/books/" + id;
    }

    // 点踩图书
    @PostMapping("/books/{id}/dislike")
    public String dislikeBook(@PathVariable("id") Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        bookService.toggleDislike(user.getId(), id);
        return "redirect:/books/" + id;
    }

    // 添加评论
    @PostMapping("/books/{id}/comment")
    public String addComment(@PathVariable("id") Long id, @RequestParam("text") String text, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        Comment comment = new Comment();
        comment.setUserId(user.getId());
        comment.setBookId(id);
        comment.setText(text);
        comment.setCreatedDate(new Date());
        logger.info("用户 {} 为图书 ID: {} 添加评论", user.getId(), id);
        bookService.addComment(comment);
        return "redirect:/books/" + id;
    }

    // 添加收藏：用户收藏图书
    @PostMapping("/books/{id}/favorite")
    public String addFavoriteBook(@PathVariable("id") Long bookId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            logger.warn("用户未登录，重定向到登录页面");
            return "redirect:/login";
        }
        logger.info("用户 {} 正在添加图书 ID {} 到收藏", user.getId(), bookId);
        bookService.addFavoriteBook(user.getId(), bookId);
        return "redirect:/books/" + bookId;
    }

    // 移除收藏：用户移除图书收藏
    @PostMapping("/books/{id}/favorite/remove")
    public String removeFavoriteBook(@PathVariable("id") Long bookId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            logger.warn("用户未登录，重定向到登录页面");
            return "redirect:/login";
        }
        logger.info("用户 {} 正在从收藏移除图书 ID {}", user.getId(), bookId);
        bookService.removeFavoriteBook(user.getId(), bookId);
        return "redirect:/books/" + bookId;
    }
}