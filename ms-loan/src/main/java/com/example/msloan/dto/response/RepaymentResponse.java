package com.example.msloan.dto.response;

import com.example.msloan.model.enums.LoanStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RepaymentResponse(
        Long id,
        Long loanId,
        BigDecimal amount,
        BigDecimal totalPaid,
        BigDecimal remainingBalance,
        LoanStatus loanStatus,
        LocalDateTime paidAt
) {
}
