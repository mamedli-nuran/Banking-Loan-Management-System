package com.example.msloan.repository;

import com.example.msloan.model.Loan;
import com.example.msloan.model.enums.LoanStatus;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByStatusIn(List<LoanStatus> statuses);

    Long countByStatus(LoanStatus status);

    @Query("select coalesce(sum(l.amount), 0) from Loan l")
    BigDecimal getTotalPrincipal();
}
