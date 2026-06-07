package com.example.msloan.dto.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisterEvent {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;


}
