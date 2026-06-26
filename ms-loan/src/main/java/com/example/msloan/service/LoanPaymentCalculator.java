package com.example.msloan.service;

import com.example.msloan.dto.response.RepaymentScheduleItemResponse;
import com.example.msloan.model.Loan;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LoanPaymentCalculator {
    private static final int MONEY_SCALE = 2;
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    public List<RepaymentScheduleItemResponse> buildSchedule(Loan loan) {
        List<RepaymentScheduleItemResponse> schedule = new ArrayList<>();
        BigDecimal balance = money(loan.getAmount());
        BigDecimal monthlyRate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(100), MATH_CONTEXT)
                .divide(BigDecimal.valueOf(12), MATH_CONTEXT);
        BigDecimal monthlyPayment = getMonthlyPayment(balance, monthlyRate, loan.getTermMonths());
        LocalDate firstDueDate = loan.getDisbursedAt().toLocalDate().plusMonths(1);

        for (int month = 1; month <= loan.getTermMonths(); month++) {
            BigDecimal interest = money(balance.multiply(monthlyRate, MATH_CONTEXT));
            BigDecimal principal = money(monthlyPayment.subtract(interest));

            if (month == loan.getTermMonths()) {
                principal = balance;
                monthlyPayment = money(principal.add(interest));
            }

            balance = money(balance.subtract(principal));

            schedule.add(new RepaymentScheduleItemResponse(
                    month,
                    firstDueDate.plusMonths(month - 1L),
                    principal,
                    interest,
                    monthlyPayment,
                    balance.max(BigDecimal.ZERO)
            ));
        }

        return schedule;
    }

    public BigDecimal getTotalPayable(Loan loan) {
        return buildSchedule(loan).stream()
                .map(RepaymentScheduleItemResponse::totalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getMonthlyPayment(BigDecimal amount, BigDecimal monthlyRate, Integer termMonths) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return money(amount.divide(BigDecimal.valueOf(termMonths), MATH_CONTEXT));
        }

        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal rateFactor = onePlusRate.pow(termMonths, MATH_CONTEXT);
        BigDecimal numerator = amount.multiply(monthlyRate, MATH_CONTEXT).multiply(rateFactor, MATH_CONTEXT);
        BigDecimal denominator = rateFactor.subtract(BigDecimal.ONE);

        return money(numerator.divide(denominator, MATH_CONTEXT));
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
