package com.example.mscustomer.controller;

import com.example.mscustomer.dto.request.CustomerRequest;
import com.example.mscustomer.dto.response.CustomerResponse;
import com.example.mscustomer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    public ResponseEntity<CustomerResponse> addNewCustomer(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerDetails(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(customerService.getCustomer(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerService.updateCustomer(id, request));
    }
}
