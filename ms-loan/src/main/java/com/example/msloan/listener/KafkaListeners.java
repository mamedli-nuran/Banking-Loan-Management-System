package com.example.msloan.listener;


import com.example.msloan.dto.event.CustomerRegisterEvent;
import com.example.msloan.mapper.CustomerMapper;
import com.example.msloan.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaListeners {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @KafkaListener(
            topics = "customer-topic",
            groupId = "customer-group",
            containerFactory = "factory")
    public void handleCustomerRegister(CustomerRegisterEvent event) {
        customerRepository.save(customerMapper.toEntity(event));
    }
}
