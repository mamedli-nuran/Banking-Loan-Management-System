package com.example.msloan.dto.response;

import com.example.msloan.model.enums.LoanStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record LoanResponse(
        Long id,
        Long customerId,
        BigDecimal amount,
        BigDecimal interestRate,
        Integer termMonths,
        LoanStatus status,
        LocalDateTime appliedAt,
        LocalDateTime approvedAt,
        LocalDateTime disbursedAt,
        List<RepaymentScheduleItemResponse> repaymentSchedule
) {
}
