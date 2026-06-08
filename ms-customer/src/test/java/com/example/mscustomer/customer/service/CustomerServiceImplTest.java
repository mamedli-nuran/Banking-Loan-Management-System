package com.example.mscustomer.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.mscustomer.customer.dto.request.CustomerRequest;
import com.example.mscustomer.customer.exception.CustomerNotFoundException;
import com.example.mscustomer.customer.model.Customer;
import com.example.mscustomer.customer.repository.CustomerRepository;
import java.time.Instant;
import java.util.Optional;

import com.example.mscustomer.customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void shouldCreateCustomer() {
        Customer savedCustomer = new Customer(1L, "John", "Doe", "john@example.com", "+123456789", Instant.parse("2026-06-07T10:15:30Z"));
        when(customerRepository.save(org.mockito.ArgumentMatchers.any(Customer.class))).thenReturn(savedCustomer);

        var response = customerService.createCustomer(new CustomerRequest(" John ", " Doe ", " John@Example.com ", " +123456789 "));

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        assertThat(customerCaptor.getValue().getEmail()).isEqualTo("john@example.com");
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenCustomerMissing() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomer(99L));
    }
}
