package com.example.mscustomer.mapper;

import com.example.mscustomer.dto.request.CustomerRequest;
import com.example.mscustomer.dto.response.CustomerResponse;
import com.example.mscustomer.model.Customer;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toResponse(Customer customer);

    Customer toEntity(CustomerRequest request);
}
