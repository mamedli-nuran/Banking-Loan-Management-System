package com.example.msloan.service.impl;

import com.example.msloan.dto.request.ApprovalRequest;
import com.example.msloan.dto.request.LoanRequest;
import com.example.msloan.dto.response.LoanResponse;
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

    private Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));

    }
}
