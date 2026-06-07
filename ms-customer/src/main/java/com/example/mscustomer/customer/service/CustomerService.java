package com.example.mscustomer.customer.service;

import com.example.mscustomer.customer.dto.request.CustomerRequest;
import com.example.mscustomer.customer.dto.response.CustomerResponse;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomer(Long customerId);

    CustomerResponse updateCustomer(Long customerId, CustomerRequest request);
}
