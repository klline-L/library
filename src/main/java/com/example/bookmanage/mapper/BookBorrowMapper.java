package com.example.bookmanage.mapper;

import com.example.bookmanage.entity.BookBorrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookBorrowMapper {
    // 插入借阅记录
    void insert(BookBorrow borrow);

    // 根据 ID 查询借阅记录
    BookBorrow findById(Long id);

    // 根据用户 ID 查询借阅记录
    List<BookBorrow> findByUserId(@Param("userId") Long userId);

    // 更新归还日期
    void updateReturnDate(@Param("borrowId") Long borrowId, @Param("returnDate") java.time.LocalDate returnDate);

    // 查询所有借阅记录
    List<BookBorrow> findAll();

    // 新增：统计每本书的借阅次数
    List<BookBorrowStats> findBorrowStats();

    // 定义内部统计类
    class BookBorrowStats {
        private Long bookId;
        private Long borrowCount;

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Long getBorrowCount() { return borrowCount; }
        public void setBorrowCount(Long borrowCount) { this.borrowCount = borrowCount; }
    }
}