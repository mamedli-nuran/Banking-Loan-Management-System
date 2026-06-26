package com.example.msloan.mapper;

import com.example.msloan.dto.request.LoanRequest;
import com.example.msloan.dto.response.LoanResponse;
import com.example.msloan.model.Customer;
import com.example.msloan.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "appliedAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "disbursedAt", ignore = true)
    Loan toEntity(LoanRequest request, Customer customer);

    @Mapping(target = "customerId", source = "loan.customer.id")
    LoanResponse toResponse(Loan loan);
}
