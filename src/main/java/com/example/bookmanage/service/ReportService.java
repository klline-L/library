package com.example.bookmanage.service;

import com.example.bookmanage.dto.BorrowStatsDTO;

import java.util.List;

public interface ReportService {
    // 获取借阅统计数据
    List<BorrowStatsDTO> getBorrowStats();

    // 生成借阅统计报告 PDF
    byte[] generateBorrowStatsPdf();
}