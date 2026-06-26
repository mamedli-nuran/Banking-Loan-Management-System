package com.example.msloan.service.impl;

import com.example.msloan.dto.response.LoanPortfolioSummaryResponse;
import com.example.msloan.dto.response.OverdueLoanResponse;
import com.example.msloan.dto.response.RepaymentScheduleItemResponse;
import com.example.msloan.model.Loan;
import com.example.msloan.model.enums.LoanStatus;
import com.example.msloan.repository.LoanRepository;
import com.example.msloan.repository.RepaymentRepository;
import com.example.msloan.service.LoanPaymentCalculator;
import com.example.msloan.service.ReportService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;
    private final LoanPaymentCalculator loanPaymentCalculator;

    @Override
    public List<OverdueLoanResponse> getOverdueLoans() {
        return loanRepository.findAllByStatusIn(List.of(LoanStatus.DISBURSED))
                .stream()
                .map(this::toOverdueResponse)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public LoanPortfolioSummaryResponse getPortfolioSummary() {
        BigDecimal totalPrincipal = loanRepository.getTotalPrincipal();
        BigDecimal totalPaid = repaymentRepository.getTotalPaid();
        BigDecimal totalPayable = loanRepository.findAllByStatusIn(List.of(LoanStatus.DISBURSED, LoanStatus.CLOSED))
                .stream()
                .map(loanPaymentCalculator::getTotalPayable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new LoanPortfolioSummaryResponse(
                loanRepository.count(),
                loanRepository.countByStatus(LoanStatus.PENDING),
                loanRepository.countByStatus(LoanStatus.APPROVED),
                loanRepository.countByStatus(LoanStatus.DISBURSED),
                loanRepository.countByStatus(LoanStatus.CLOSED),
                loanRepository.countByStatus(LoanStatus.REJECTED),
                totalPrincipal,
                totalPaid,
                totalPayable.subtract(totalPaid).max(BigDecimal.ZERO)
        );
    }

    private Optional<OverdueLoanResponse> toOverdueResponse(Loan loan) {
        LocalDate today = LocalDate.now();
        List<RepaymentScheduleItemResponse> overdueItems = loanPaymentCalculator.buildSchedule(loan)
                .stream()
                .filter(item -> item.dueDate().isBefore(today))
                .toList();

        if (overdueItems.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal totalDue = overdueItems.stream()
                .map(RepaymentScheduleItemResponse::totalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = repaymentRepository.getTotalPaidByLoanId(loan.getId());
        BigDecimal overdueAmount = totalDue.subtract(totalPaid);

        if (overdueAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        LocalDate oldestDueDate = getOldestUnpaidDueDate(overdueItems, totalPaid);

        return Optional.of(new OverdueLoanResponse(
                loan.getId(),
                loan.getCustomer().getId(),
                loan.getAmount(),
                totalDue,
                totalPaid,
                overdueAmount,
                oldestDueDate
        ));
    }

    private LocalDate getOldestUnpaidDueDate(List<RepaymentScheduleItemResponse> overdueItems, BigDecimal totalPaid) {
        BigDecimal cumulativeDue = BigDecimal.ZERO;

        for (RepaymentScheduleItemResponse item : overdueItems) {
            cumulativeDue = cumulativeDue.add(item.totalPayment());

            if (cumulativeDue.compareTo(totalPaid) > 0) {
                return item.dueDate();
            }
        }

        return overdueItems.get(overdueItems.size() - 1).dueDate();
    }
}
