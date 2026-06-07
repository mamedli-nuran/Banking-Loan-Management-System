package com.example.msloan.listener;


import com.example.msloan.dto.event.CustomerRegisterEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {
    @KafkaListener(
            topics = "customer-topic",
            groupId = "customer-group",
            containerFactory = "factory")
    public void handleCustomerRegister(CustomerRegisterEvent event) {
        // Здесь ваша бизнес-логика
        System.out.println("Получено событие регистрации клиента: " + event.toString());
    }
}
