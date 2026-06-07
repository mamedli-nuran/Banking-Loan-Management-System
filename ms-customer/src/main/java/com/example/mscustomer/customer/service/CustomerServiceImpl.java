package com.example.mscustomer.customer.service;

import com.example.mscustomer.customer.dto.event.CustomerRegisterEvent;
import com.example.mscustomer.customer.dto.request.CustomerRequest;
import com.example.mscustomer.customer.dto.response.CustomerResponse;
import com.example.mscustomer.customer.exception.CustomerNotFoundException;
import com.example.mscustomer.customer.model.Customer;
import com.example.mscustomer.customer.repository.CustomerRepository;
import com.example.mscustomer.mapper.KafkaEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.mscustomer.mapper.KafkaEventMapper.KAFKA_EVENT_MAPPER;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final KafkaTemplate<String, CustomerRegisterEvent> kafkaTemplate;
    private final KafkaEventMapper kafkaEventMapper;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        applyRequest(customer, request);
        Customer savedCustomer = customerRepository.save(customer);
        CustomerRegisterEvent event =kafkaEventMapper.toEvent(savedCustomer);
        System.out.println(event);
        kafkaTemplate.send("customer-topic", kafkaEventMapper.toEvent(savedCustomer));
        return toResponse(savedCustomer);
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
        applyRequest(customer, request);
        Customer savedCustomer = customerRepository.save(customer);
        return toResponse(savedCustomer);
    }

    private void applyRequest(Customer customer, CustomerRequest request) {
        customer.setFirstName(request.firstName().trim());
        customer.setLastName(request.lastName().trim());
        customer.setEmail(request.email().trim().toLowerCase());
        customer.setPhone(request.phone() == null || request.phone().isBlank() ? null : request.phone().trim());
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
