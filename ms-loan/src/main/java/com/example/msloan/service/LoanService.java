package com.example.msloan.service;

import com.example.msloan.dto.request.ApprovalRequest;
import com.example.msloan.dto.request.LoanRequest;
import com.example.msloan.dto.request.RepaymentRequest;
import com.example.msloan.dto.response.LoanResponse;
import com.example.msloan.dto.response.RepaymentResponse;
import com.example.msloan.dto.response.RepaymentScheduleItemResponse;
import java.util.List;

public interface LoanService {
    LoanResponse apply(LoanRequest request);

    LoanResponse getById(Long id);

    LoanResponse approve(Long id, ApprovalRequest request);

    LoanResponse disburse(Long id);

    RepaymentResponse repay(Long id, RepaymentRequest request);

    List<RepaymentScheduleItemResponse> getSchedule(Long id);
}
