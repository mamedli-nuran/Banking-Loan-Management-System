package com.example.mscustomer.service;

import com.example.mscustomer.dto.request.CustomerRequest;
import com.example.mscustomer.dto.response.CustomerResponse;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomer(Long customerId);

    CustomerResponse updateCustomer(Long customerId, CustomerRequest request);
}
