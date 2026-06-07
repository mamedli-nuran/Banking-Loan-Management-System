package com.example.msloan.mapper;

import com.example.msloan.dto.event.CustomerRegisterEvent;
import com.example.msloan.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerRegisterEvent event);
}
