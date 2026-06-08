package com.example.mscustomer.customer.service.impl;

import com.example.mscustomer.customer.dto.event.CustomerRegisterEvent;
import com.example.mscustomer.customer.dto.request.CustomerRequest;
import com.example.mscustomer.customer.dto.response.CustomerResponse;
import com.example.mscustomer.customer.exception.CustomerNotFoundException;
import com.example.mscustomer.customer.model.Customer;
import com.example.mscustomer.customer.repository.CustomerRepository;
import com.example.mscustomer.customer.service.CustomerService;
import com.example.mscustomer.mapper.CustomerMapper;
import com.example.mscustomer.mapper.KafkaEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
        CustomerRegisterEvent event =kafkaEventMapper.toEvent(savedCustomer);
        System.out.println(event);
        kafkaTemplate.send("customer-topic", kafkaEventMapper.toEvent(savedCustomer));
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .map(this::toResponse)
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
        return customerMapper.toResponse(customer);
    }


    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCreatedAt()
        );
    }
}
