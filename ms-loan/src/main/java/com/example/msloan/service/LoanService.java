package com.example.msloan.service;

import com.example.msloan.dto.request.ApprovalRequest;
import com.example.msloan.dto.request.LoanApplicationRequest;
import com.example.msloan.dto.response.LoanResponse;

public interface LoanService {
    LoanResponse apply(LoanApplicationRequest request);

    LoanResponse getById(Long id);

    LoanResponse approve(Long id, ApprovalRequest request);

    LoanResponse disburse(Long id);
}
