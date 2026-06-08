package com.example.msloan.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RepaymentScheduleItemResponse(
        Integer paymentNumber,
        LocalDate dueDate,
        BigDecimal principal,
        BigDecimal interest,
        BigDecimal totalPayment,
        BigDecimal remainingBalance
) {
}
