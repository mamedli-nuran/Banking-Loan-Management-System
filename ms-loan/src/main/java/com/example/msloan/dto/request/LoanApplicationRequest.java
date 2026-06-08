package com.example.msloan.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record LoanApplicationRequest(
        @NotNull Long customerId,
        @NotNull @Positive BigDecimal amount,
        @NotNull @PositiveOrZero BigDecimal interestRate,
        @NotNull @Positive Integer termMonths
) {
}
