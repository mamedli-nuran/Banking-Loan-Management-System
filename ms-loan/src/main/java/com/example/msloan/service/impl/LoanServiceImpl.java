package com.example.msloan.service.impl;

import com.example.msloan.dto.request.ApprovalRequest;
import com.example.msloan.dto.request.LoanApplicationRequest;
import com.example.msloan.dto.response.LoanResponse;
import com.example.msloan.dto.response.RepaymentScheduleItemResponse;
import com.example.msloan.exception.CustomerNotFoundException;
import com.example.msloan.exception.LoanNotFoundException;
import com.example.msloan.exception.LoanStatusException;
import com.example.msloan.mapper.LoanMapper;
import com.example.msloan.model.Customer;
import com.example.msloan.model.Loan;
import com.example.msloan.model.enums.LoanStatus;
import com.example.msloan.repository.CustomerRepository;
import com.example.msloan.repository.LoanRepository;
import com.example.msloan.service.LoanService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanMapper loanMapper;

    @Override
    public LoanResponse apply(LoanApplicationRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.customerId()));

        Loan loan = loanMapper.toEntity(request);
        loan.setCustomer(customer);
        loan.setStatus(LoanStatus.PENDING);
        loan.setAppliedAt(LocalDateTime.now());

        return toResponse(loanRepository.save(loan));
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponse getById(Long id) {
        return toResponse(findLoan(id));
    }

    @Override
    public LoanResponse approve(Long id, ApprovalRequest request) {
        Loan loan = findLoan(id);
        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new LoanStatusException("Only pending loans can be approved or rejected");
        }

        loan.setStatus(Boolean.TRUE.equals(request.approved()) ? LoanStatus.APPROVED : LoanStatus.REJECTED);
        loan.setApprovedAt(LocalDateTime.now());

        return toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse disburse(Long id) {
        Loan loan = findLoan(id);
        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new LoanStatusException("Only approved loans can be disbursed");
        }

        loan.setStatus(LoanStatus.DISBURSED);
        loan.setDisbursedAt(LocalDateTime.now());

        return toResponse(loanRepository.save(loan));
    }

    private Loan findLoan(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
    }

    private List<RepaymentScheduleItemResponse> generateRepaymentSchedule(Loan loan) {
        if (loan.getStatus() != LoanStatus.DISBURSED || loan.getDisbursedAt() == null) {
            return Collections.emptyList();
        }

        BigDecimal monthlyRate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = calculateMonthlyPayment(loan.getAmount(), monthlyRate, loan.getTermMonths());
        BigDecimal balance = loan.getAmount();
        LocalDate firstDueDate = loan.getDisbursedAt().toLocalDate().plusMonths(1);
        List<RepaymentScheduleItemResponse> schedule = new ArrayList<>();

        for (int paymentNumber = 1; paymentNumber <= loan.getTermMonths(); paymentNumber++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = monthlyPayment.subtract(interest).setScale(2, RoundingMode.HALF_UP);

            if (paymentNumber == loan.getTermMonths()) {
                principal = balance;
                monthlyPayment = principal.add(interest).setScale(2, RoundingMode.HALF_UP);
            }

            balance = balance.subtract(principal).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            schedule.add(new RepaymentScheduleItemResponse(
                    paymentNumber,
                    firstDueDate.plusMonths(paymentNumber - 1L),
                    principal,
                    interest,
                    monthlyPayment,
                    balance
            ));
        }

        return schedule;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal monthlyRate, int termMonths) {
        if (monthlyRate.signum() == 0) {
            return amount.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        }

        double rate = monthlyRate.doubleValue();
        double coefficient = rate * Math.pow(1 + rate, termMonths) / (Math.pow(1 + rate, termMonths) - 1);
        return amount.multiply(BigDecimal.valueOf(coefficient)).setScale(2, RoundingMode.HALF_UP);
    }

    private LoanResponse toResponse(Loan loan) {
        return loanMapper.toResponse(loan, generateRepaymentSchedule(loan));
    }
}
