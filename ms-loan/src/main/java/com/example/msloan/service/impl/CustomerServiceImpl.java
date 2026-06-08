package com.example.msloan.service.impl;


import com.example.msloan.dto.event.CustomerRegisterEvent;
import com.example.msloan.mapper.CustomerMapper;
import com.example.msloan.repository.CustomerRepository;
import com.example.msloan.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @KafkaListener(
            topics = "customer-topic",
            groupId = "customer-group",
            containerFactory = "factory")
    public void handleCustomerRegister(CustomerRegisterEvent event) {
        customerRepository.save(customerMapper.toEntity(event));
        log.info("Customer with id {} successfully saved in database", event.getId());
    }
}
