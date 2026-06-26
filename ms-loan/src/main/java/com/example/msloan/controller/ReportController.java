package com.example.msloan.controller;

import com.example.msloan.dto.response.LoanPortfolioSummaryResponse;
import com.example.msloan.dto.response.OverdueLoanResponse;
import com.example.msloan.service.ReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/reports", "/api/reports"})
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/loans/overdue")
    public ResponseEntity<List<OverdueLoanResponse>> getOverdueLoans() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reportService.getOverdueLoans());
    }

    @GetMapping("/loans/portfolio")
    public ResponseEntity<LoanPortfolioSummaryResponse> getPortfolioSummary() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reportService.getPortfolioSummary());
    }
}
