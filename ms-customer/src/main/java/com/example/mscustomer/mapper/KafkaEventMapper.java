package com.example.mscustomer.mapper;

import com.example.mscustomer.dto.event.CustomerRegisterEvent;
import com.example.mscustomer.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KafkaEventMapper {

    CustomerRegisterEvent toEvent (Customer customer);
}
