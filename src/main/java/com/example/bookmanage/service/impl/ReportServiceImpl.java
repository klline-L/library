package com.example.bookmanage.service.impl;

import com.example.bookmanage.dto.BorrowStatsDTO;
import com.example.bookmanage.mapper.BookBorrowMapper;
import com.example.bookmanage.service.BookService;
import com.example.bookmanage.service.ReportService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private BookBorrowMapper bookBorrowMapper;

    @Autowired
    private BookService bookService;

    @Override
    public List<BorrowStatsDTO> getBorrowStats() {
        // 获取借阅统计数据并转换为 DTO
        List<BookBorrowMapper.BookBorrowStats> stats = bookBorrowMapper.findBorrowStats();
        return stats.stream().map(stat -> {
            BorrowStatsDTO dto = new BorrowStatsDTO();
            dto.setBookTitle(bookService.getBookById(stat.getBookId()).getTitle());
            dto.setBorrowCount(stat.getBorrowCount());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public byte[] generateBorrowStatsPdf() {
        // 使用 iText 生成借阅统计 PDF
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            document.add(new Paragraph("借阅统计报告"));
            for (BorrowStatsDTO stat : getBorrowStats()) {
                document.add(new Paragraph(stat.getBookTitle() + ": " + stat.getBorrowCount() + " 次"));
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("生成 PDF 失败", e);
        }
    }
}