package com.example.mscustomer.mapper;

import com.example.mscustomer.customer.dto.request.CustomerRequest;
import com.example.mscustomer.customer.dto.response.CustomerResponse;
import com.example.mscustomer.customer.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toResponse(Customer customer);

    Customer toEntity(CustomerRequest request);
}
