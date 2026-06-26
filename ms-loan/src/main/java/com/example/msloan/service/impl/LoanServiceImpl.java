package com.example.msloan.service.impl;

import com.example.msloan.dto.request.ApprovalRequest;
import com.example.msloan.dto.request.LoanRequest;
import com.example.msloan.dto.request.RepaymentRequest;
import com.example.msloan.dto.response.LoanResponse;
import com.example.msloan.dto.response.RepaymentResponse;
import com.example.msloan.dto.response.RepaymentScheduleItemResponse;
import com.example.msloan.exception.CustomerNotFoundException;
import com.example.msloan.exception.LoanNotFoundException;
import com.example.msloan.exception.LoanStatusException;
import com.example.msloan.mapper.LoanMapper;
import com.example.msloan.model.Customer;
import com.example.msloan.model.Loan;
import com.example.msloan.model.Repayment;
import com.example.msloan.model.enums.LoanStatus;
import com.example.msloan.repository.CustomerRepository;
import com.example.msloan.repository.LoanRepository;
import com.example.msloan.repository.RepaymentRepository;
import com.example.msloan.service.LoanPaymentCalculator;
import com.example.msloan.service.LoanService;

import java.math.BigDecimal;
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
    private final RepaymentRepository repaymentRepository;
    private final LoanMapper loanMapper;
    private final LoanPaymentCalculator loanPaymentCalculator;

    @Override
    public LoanResponse apply(LoanRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.customerId()));

        Loan loan = loanMapper.toEntity(request, customer);
        loan = loanRepository.save(loan);

        return loanMapper.toResponse(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponse getById(Long id) {
        Loan loan = getLoanById(id);

        return loanMapper.toResponse(loan);
    }

    @Override
    public LoanResponse approve(Long id, ApprovalRequest request) {
        Loan loan = getPendingLoan(id);

        if (request.approved()) {
            loan.approve();
        } else {
            loan.reject();
        }

        return loanMapper.toResponse(loan);
    }

    @Override
    public LoanResponse disburse(Long id) {
        Loan loan = getApprovedLoan(id);

        loan.disburse();

        return loanMapper.toResponse(loan);
    }

    @Override
    public RepaymentResponse repay(Long id, RepaymentRequest request) {
        Loan loan = getPayableLoan(id);
        BigDecimal totalPaidBeforePayment = repaymentRepository.getTotalPaidByLoanId(id);
        BigDecimal totalPayable = loanPaymentCalculator.getTotalPayable(loan);
        BigDecimal remainingBeforePayment = totalPayable.subtract(totalPaidBeforePayment);

        if (request.amount().compareTo(remainingBeforePayment) > 0) {
            throw new LoanStatusException("Payment amount cannot be greater than remaining balance");
        }

        Repayment repayment = repaymentRepository.save(Repayment.builder()
                .loan(loan)
                .amount(request.amount())
                .build());

        BigDecimal totalPaid = totalPaidBeforePayment.add(request.amount());
        BigDecimal remainingBalance = totalPayable.subtract(totalPaid);

        if (remainingBalance.compareTo(BigDecimal.ZERO) == 0) {
            loan.close();
        }

        return new RepaymentResponse(
                repayment.getId(),
                loan.getId(),
                repayment.getAmount(),
                totalPaid,
                remainingBalance,
                loan.getStatus(),
                repayment.getPaidAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepaymentScheduleItemResponse> getSchedule(Long id) {
        Loan loan = getLoanWithSchedule(id);

        return loanPaymentCalculator.buildSchedule(loan);
    }

    private Loan getPendingLoan(Long id) {
        Loan loan = getLoanById(id);

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new LoanStatusException("Only pending loans can be approved or rejected");
        }

        return loan;
    }

    private Loan getApprovedLoan(Long id) {
        Loan loan = getLoanById(id);

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new LoanStatusException("Only approved loans can be disbursed");
        }

        return loan;
    }

    private Loan getPayableLoan(Long id) {
        Loan loan = getLoanById(id);

        if (loan.getStatus() != LoanStatus.DISBURSED) {
            throw new LoanStatusException("Only disbursed loans can be repaid");
        }

        return loan;
    }

    private Loan getLoanWithSchedule(Long id) {
        Loan loan = getLoanById(id);

        if (loan.getStatus() != LoanStatus.DISBURSED && loan.getStatus() != LoanStatus.CLOSED) {
            throw new LoanStatusException("Only disbursed or closed loans have a repayment schedule");
        }

        return loan;
    }

    private Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));

    }
}
