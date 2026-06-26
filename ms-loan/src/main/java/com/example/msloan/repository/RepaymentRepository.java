package com.example.msloan.repository;

import com.example.msloan.model.Repayment;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

    @Query("select coalesce(sum(r.amount), 0) from Repayment r where r.loan.id = :loanId")
    BigDecimal getTotalPaidByLoanId(@Param("loanId") Long loanId);

    @Query("select coalesce(sum(r.amount), 0) from Repayment r")
    BigDecimal getTotalPaid();
}
