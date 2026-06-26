package com.example.msloan.dto.response;

import java.math.BigDecimal;

public record LoanPortfolioSummaryResponse(
        Long totalLoans,
        Long pendingLoans,
        Long approvedLoans,
        Long disbursedLoans,
        Long closedLoans,
        Long rejectedLoans,
        BigDecimal totalPrincipal,
        BigDecimal totalPaid,
        BigDecimal outstandingBalance
) {
}
