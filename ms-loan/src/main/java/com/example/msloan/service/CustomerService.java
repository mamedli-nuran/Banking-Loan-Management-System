package com.example.msloan.service;

import com.example.msloan.dto.event.CustomerRegisterEvent;

public interface CustomerService {
    void handleCustomerRegister(CustomerRegisterEvent event);
}
