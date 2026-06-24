package com.example.mscustomer.service.impl;

import com.example.mscustomer.dto.event.CustomerRegisterEvent;
import com.example.mscustomer.dto.request.CustomerRequest;
import com.example.mscustomer.dto.response.CustomerResponse;
import com.example.mscustomer.exception.CustomerNotFoundException;
import com.example.mscustomer.model.Customer;
import com.example.mscustomer.repository.CustomerRepository;
import com.example.mscustomer.service.CustomerService;

import com.example.mscustomer.mapper.CustomerMapper;
import com.example.mscustomer.mapper.KafkaEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final KafkaTemplate<String, CustomerRegisterEvent> kafkaTemplate;
    private final KafkaEventMapper kafkaEventMapper;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer savedCustomer = customerRepository.save(customerMapper.toEntity(request));

        kafkaTemplate.send("customer-topic", kafkaEventMapper.toEvent(savedCustomer));
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    @Override
    public CustomerResponse updateCustomer(Long customerId, CustomerRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customerRepository.save(customer);

        kafkaTemplate.send("customer-topic", kafkaEventMapper.toEvent(customer));
        return customerMapper.toResponse(customer);
    }
}
