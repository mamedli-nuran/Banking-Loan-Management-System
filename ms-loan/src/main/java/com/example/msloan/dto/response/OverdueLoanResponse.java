package com.example.msloan.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OverdueLoanResponse(
        Long loanId,
        Long customerId,
        BigDecimal loanAmount,
        BigDecimal totalDue,
        BigDecimal totalPaid,
        BigDecimal overdueAmount,
        LocalDate oldestDueDate
) {
}
