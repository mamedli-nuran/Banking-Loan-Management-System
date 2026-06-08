package com.example.mscustomer.dto.event;


public record CustomerRegisterEvent(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
}
