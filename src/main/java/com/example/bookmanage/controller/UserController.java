package com.example.bookmanage.controller;

import com.example.bookmanage.dto.BorrowedBookDTO;
import com.example.bookmanage.entity.Book;
import com.example.bookmanage.entity.User;
import com.example.bookmanage.service.BookService;
import com.example.bookmanage.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final BookService bookService;
    private final UserService userService;

    public UserController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    // 显示注册页面
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // 处理注册请求
    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            user.setRegisterDate(LocalDate.now().atStartOfDay());
            userService.register(user);
            logger.info("用户 {} 注册成功", user.getUsername());
            redirectAttributes.addFlashAttribute("success", "注册成功，请登录！");
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("用户 {} 注册失败: {}", user.getUsername(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", "注册失败，请重试！");
            return "redirect:/register";
        }
    }

    // 显示登录页面
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // 处理登录请求，根据角色跳转不同页面
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("role") String role,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        User user = userService.validateUser(username, password);
        if (user != null && user.getRole().equalsIgnoreCase(role)) {
            logger.info("用户 {} 登录成功，角色: {}", username, role);
            session.setAttribute("loggedInUser", user);
            if ("admin".equalsIgnoreCase(role)) {
                return "redirect:/admin/dashboard"; // 管理员跳转到仪表板
            } else {
                return "redirect:/"; // 普通用户跳转到首页
            }
        } else {
            logger.warn("用户 {} 登录失败，尝试角色: {}", username, role);
            redirectAttributes.addFlashAttribute("error", "用户名、密码或角色错误，请重试！");
            return "redirect:/login";
        }
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        logger.info("用户退出登录");
        return "redirect:/login";
    }

    // 用户仪表板
    @GetMapping("/user/dashboard")
    public String userDashboard(@SessionAttribute(value = "loggedInUser", required = false) User user, Model model) {
        if (user == null) {
            logger.warn("用户未认证，重定向到登录页面");
            return "redirect:/login";
        }
        List<BorrowedBookDTO> borrowedBooks = bookService.getBorrowedBooksByUserId(user.getId())
                .stream()
                .filter(b -> b.getBorrow().getReturnDate() == null)
                .collect(Collectors.toList());

        List<BorrowedBookDTO> historyBooks = bookService.getBorrowedBooksByUserId(user.getId())
                .stream()
                .filter(b -> b.getBorrow().getReturnDate() != null)
                .collect(Collectors.toList());

        List<Book> favoriteBooks = bookService.getFavoriteBooksByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("borrowedBooks", borrowedBooks);
        model.addAttribute("historyBooks", historyBooks);
        model.addAttribute("favoriteBooks", favoriteBooks);
        logger.info("为用户 {} 加载仪表板，借阅: {}, 历史: {}, 收藏: {}",
                user.getUsername(), borrowedBooks.size(), historyBooks.size(), favoriteBooks.size());
        return "user-dashboard";
    }

    // 归还图书
    @PostMapping("/book_borrows/{id}/return")
    public String returnBook(@PathVariable("id") Long borrowId, @SessionAttribute(value = "loggedInUser", required = false) User user) {
        if (user == null) {
            logger.warn("用户未认证，重定向到登录页面");
            return "redirect:/login";
        }
        bookService.returnBook(borrowId, user.getId());
        logger.info("用户 {} 归还了借阅 ID 为 {} 的图书", user.getUsername(), borrowId);
        return "redirect:/user/dashboard";
    }

    // 显示编辑个人资料页面
    @GetMapping("/user/edit")
    public String showEditProfile(@SessionAttribute(value = "loggedInUser", required = false) User user, Model model) {
        if (user == null) {
            logger.warn("用户未认证，重定向到登录页面");
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user-edit";
    }

    // 更新个人资料
    @PostMapping("/user/edit")
    public String updateProfile(@ModelAttribute User updatedUser,
                                @SessionAttribute("loggedInUser") User user,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        updatedUser.setId(user.getId());
        updatedUser.setPassword(user.getPassword()); // 不允许通过此接口修改密码
        updatedUser.setEmail(updatedUser.getEmail() != null ? updatedUser.getEmail() : user.getEmail());
        updatedUser.setRole(user.getRole()); // 保留原有 role 值，避免 null

        String newUsername = updatedUser.getUsername();
        if (!newUsername.equals(user.getUsername())) {
            LocalDate lastUpdate = user.getLastUsernameUpdate();
            LocalDate now = LocalDate.now();
            if (lastUpdate != null && lastUpdate.plusDays(30).isAfter(now)) {
                redirectAttributes.addFlashAttribute("error", "用户名每30天只能修改一次，上次修改时间: " + lastUpdate);
                return "redirect:/user/edit";
            }
            updatedUser.setLastUsernameUpdate(now);
        } else {
            updatedUser.setUsername(user.getUsername());
        }

        userService.updateUser(updatedUser);
        session.setAttribute("loggedInUser", updatedUser);
        logger.info("用户 {} 更新了个人资料", user.getUsername());
        return "redirect:/user/dashboard";
    }

    // 从收藏中移除图书
    @PostMapping("/user/favorite/remove/{id}")
    public String removeFavoriteBook(@PathVariable("id") Long bookId, @SessionAttribute("loggedInUser") User user) {
        if (user == null) {
            logger.warn("用户未登录，重定向到登录页面");
            return "redirect:/login";
        }
        logger.info("用户 {} 从收藏移除图书 ID {}", user.getId(), bookId);
        bookService.removeFavoriteBook(user.getId(), bookId);
        return "redirect:/user/dashboard";
    }
}





