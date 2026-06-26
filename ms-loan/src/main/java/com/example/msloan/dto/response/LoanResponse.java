package com.example.msloan.dto.response;

import com.example.msloan.model.enums.LoanStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoanResponse(
        Long id,
        Long customerId,
        BigDecimal amount,
        BigDecimal interestRate,
        Integer termMonths,
        LoanStatus status,
        LocalDateTime appliedAt,
        LocalDateTime approvedAt,
        LocalDateTime disbursedAt
) {
}
