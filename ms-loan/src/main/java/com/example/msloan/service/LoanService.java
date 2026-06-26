package com.example.msloan.service;

import com.example.msloan.dto.request.ApprovalRequest;
import com.example.msloan.dto.request.LoanRequest;
import com.example.msloan.dto.response.LoanResponse;

public interface LoanService {
    LoanResponse apply(LoanRequest request);

    LoanResponse getById(Long id);

    LoanResponse approve(Long id, ApprovalRequest request);

    LoanResponse disburse(Long id);
}
