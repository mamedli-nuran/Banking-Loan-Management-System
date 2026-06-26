package com.example.msloan.service;

import com.example.msloan.dto.response.LoanPortfolioSummaryResponse;
import com.example.msloan.dto.response.OverdueLoanResponse;
import java.util.List;

public interface ReportService {
    List<OverdueLoanResponse> getOverdueLoans();

    LoanPortfolioSummaryResponse getPortfolioSummary();
}
