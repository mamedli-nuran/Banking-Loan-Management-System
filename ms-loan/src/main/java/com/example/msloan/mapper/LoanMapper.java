package com.example.msloan.mapper;

import com.example.msloan.dto.request.LoanApplicationRequest;
import com.example.msloan.dto.response.LoanResponse;
import com.example.msloan.dto.response.RepaymentScheduleItemResponse;
import com.example.msloan.model.Loan;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "appliedAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "disbursedAt", ignore = true)
    Loan toEntity(LoanApplicationRequest request);

    @Mapping(target = "customerId", source = "loan.customer.id")
    @Mapping(target = "repaymentSchedule", source = "repaymentSchedule")
    LoanResponse toResponse(Loan loan, List<RepaymentScheduleItemResponse> repaymentSchedule);
}
