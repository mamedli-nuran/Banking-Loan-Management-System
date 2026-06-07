package com.example.mscustomer.mapper;

import com.example.mscustomer.customer.dto.event.CustomerRegisterEvent;
import com.example.mscustomer.customer.dto.response.CustomerResponse;
import com.example.mscustomer.customer.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface KafkaEventMapper {

    CustomerRegisterEvent toEvent (Customer customer);
}
