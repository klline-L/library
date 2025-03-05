package com.example.bookmanage.controller;

import com.example.bookmanage.dto.SettingsDTO;
import com.example.bookmanage.entity.Book;
import com.example.bookmanage.entity.User;
import com.example.bookmanage.service.AdminLogService;
import com.example.bookmanage.service.BookService;
import com.example.bookmanage.service.ReportService;
import com.example.bookmanage.service.SettingsService;
import com.example.bookmanage.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private AdminLogService adminLogService;

    // 检查用户是否为管理员：限制管理功能访问权限
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "admin".equals(user.getRole());
    }

    // 管理员仪表板页面：显示图书和用户总数
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("totalBooks", bookService.getAllBooks().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        return "admin/dashboard";
    }

    // 书籍管理页面：列出所有图书，支持分页显示
    @GetMapping("/books")
    public String manageBooks(HttpSession session,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        // 计算分页偏移量
        int offset = (page - 1) * size;
        // 获取分页后的图书列表
        List<Book> books = bookService.getBooksByPage(page, size, "");
        int totalBooks = bookService.getAllBooks().size();
        int totalPages = (int) Math.ceil((double) totalBooks / size);

        // 设置分页参数
        model.addAttribute("books", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("hasPrevious", page > 1);
        model.addAttribute("hasNext", page < totalPages && !books.isEmpty());

        return "admin/books";
    }

    // 显示添加图书表单：管理员添加新图书
    @GetMapping("/books/add")
    public String showAddBookForm(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("book", new Book());
        return "admin/book-add";
    }

    // 添加图书：处理图书添加请求
    @PostMapping("/books/add")
    public String addBook(@ModelAttribute Book book, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");
        bookService.addBook(book);
        adminLogService.logAction(admin.getId(), "ADD_BOOK", "添加图书: " + book.getTitle());
        return "redirect:/admin/books";
    }

    // 批量删除书籍：根据选中 ID 删除多本书
    @PostMapping("/books/delete")
    public String deleteBooks(@RequestParam("bookIds") List<Long> bookIds, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");
        bookService.deleteBooks(bookIds);
        adminLogService.logAction(admin.getId(), "DELETE_BOOKS", "删除图书: " + bookIds);
        return "redirect:/admin/books";
    }

    // 单本删除图书：根据 ID 删除单本书
    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");
        bookService.deleteBook(id);
        adminLogService.logAction(admin.getId(), "DELETE_BOOK", "删除图书 ID: " + id);
        return "redirect:/admin/books";
    }

    // 批量编辑书籍：保存多本书的修改
    @PostMapping("/books/update")
    public String updateBooks(@ModelAttribute("books") List<Book> books, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");
        if (books != null && !books.isEmpty()) {
            bookService.updateBooks(books);
            adminLogService.logAction(admin.getId(), "UPDATE_BOOKS",
                    "更新图书: " + books.stream().map(Book::getId).collect(Collectors.toList()));
        } else {
            logger.warn("批量更新图书时，传入的图书列表为空");
        }
        return "redirect:/admin/books";
    }

    // 单本图书编辑页面：显示单本书的编辑表单
    @GetMapping("/books/edit/{id}")
    public String editBook(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Book book = bookService.getBookById(id);
        if (book == null) {
            return "redirect:/admin/books";
        }
        model.addAttribute("book", book);
        return "admin/book-edit";
    }

    // 保存单本图书编辑：处理单本书的保存请求
    @PostMapping("/books/update-single")
    public String updateSingleBook(@ModelAttribute Book book, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");
        bookService.updateBook(book);
        adminLogService.logAction(admin.getId(), "UPDATE_BOOK", "更新图书 ID: " + book.getId());
        return "redirect:/admin/books";
    }

    // 用户管理页面：列出所有用户
    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    // 用户详情页面：显示单个用户信息
    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/user-detail";
    }

    // 添加用户：处理用户添加请求
    @PostMapping("/users/add")
    public String addUser(@ModelAttribute User user, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");
        userService.register(user);
        adminLogService.logAction(admin.getId(), "ADD_USER", "添加用户: " + user.getUsername());
        return "redirect:/admin/users";
    }

    // 更新用户：处理用户更新请求
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");
        User existingUser = userService.getUserById(user.getId());
        if (existingUser != null) {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }
        }
        userService.updateUser(user);
        adminLogService.logAction(admin.getId(), "UPDATE_USER", "更新用户: " + user.getUsername());
        return "redirect:/admin/users";
    }

    // 删除用户：根据 ID 删除用户
    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam("userId") Long userId, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");
        userService.deleteUser(userId);
        adminLogService.logAction(admin.getId(), "DELETE_USER", "删除用户 ID: " + userId);
        return "redirect:/admin/users";
    }

    // 报告生成页面：显示借阅统计
    @GetMapping("/reports")
    public String reports(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("borrowStats", reportService.getBorrowStats());
        return "admin/reports";
    }

    // 导出借阅统计报告：生成 PDF 文件
    @GetMapping("/reports/borrow-stats/export")
    public ResponseEntity<byte[]> exportBorrowStats(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }
        byte[] pdfContent = reportService.generateBorrowStatsPdf();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=borrow-stats.pdf")
                .body(pdfContent);
    }

    // 系统设置页面：显示系统设置
    @GetMapping("/settings")
    public String settings(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("settings", settingsService.getSettings());
        return "admin/settings";
    }

    // 更新系统设置：处理设置更新请求
    @PostMapping("/settings")
    public String updateSettings(@ModelAttribute SettingsDTO settingsDTO, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");

        // 验证 finePerDay
        if (settingsDTO.getFinePerDay() == null || settingsDTO.getFinePerDay() < 0 || settingsDTO.getFinePerDay() > 999.99) {
            model.addAttribute("error", "逾期罚款必须在 0 到 999.99 之间");
            model.addAttribute("settings", settingsDTO);
            return "admin/settings";
        }

        settingsService.updateSettings(settingsDTO);
        adminLogService.logAction(admin.getId(), "UPDATE_SETTINGS", "更新借阅期限: " + settingsDTO.getBorrowDays());
        redirectAttributes.addFlashAttribute("success", "设置更新成功！");
        return "redirect:/admin/settings";
    }

    // 活动日志页面：显示管理员操作日志
    @GetMapping("/logs")
    public String logs(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("logs", adminLogService.getAllLogs());
        return "admin/logs";
    }

    // 批量删除日志：根据选中 ID 删除多条日志
    @PostMapping("/logs/delete")
    public String deleteLogs(@RequestParam("logIds") List<Long> logIds, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");
        adminLogService.deleteLogs(logIds);
        adminLogService.logAction(admin.getId(), "DELETE_LOGS", "删除日志: " + logIds);
        return "redirect:/admin/logs";
    }

    // 删除日志：根据 ID 删除单条日志
    @PostMapping("/logs/delete/{id}")
    public String deleteLog(@PathVariable("id") Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User admin = (User) session.getAttribute("loggedInUser");
        adminLogService.deleteLog(id);
        adminLogService.logAction(admin.getId(), "DELETE_LOG", "删除日志 ID: " + id);
        return "redirect:/admin/logs";
    }

    // 退出登录：清除 session 并重定向到登录页
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}