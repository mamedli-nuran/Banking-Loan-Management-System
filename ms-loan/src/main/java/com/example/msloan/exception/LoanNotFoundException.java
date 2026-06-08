package com.example.msloan.exception;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(Long loanId) {
        super("Loan not found with id: " + loanId);
    }
}
