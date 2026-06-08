package com.example.mscustomer.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mscustomer.controller.CustomerController;
import com.example.mscustomer.dto.request.CustomerRequest;
import com.example.mscustomer.dto.response.CustomerResponse;
import com.example.mscustomer.service.CustomerService;
import java.time.Instant;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    void shouldCreateCustomer() throws Exception {
        given(customerService.createCustomer(any(CustomerRequest.class)))
                .willReturn(new CustomerResponse(1L, "John", "Doe", "john@example.com", "+123456789", Instant.parse("2026-06-07T10:15:30Z")));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "email": "john@example.com",
                                  "phone": "+123456789"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(customerService).createCustomer(any(CustomerRequest.class));
    }

    @Test
    void shouldGetCustomer() throws Exception {
        given(customerService.getCustomer(1L))
                .willReturn(new CustomerResponse(1L, "John", "Doe", "john@example.com", "+123456789", Instant.parse("2026-06-07T10:15:30Z")));

        mockMvc.perform(get("/api/customers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(customerService).getCustomer(1L);
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        given(customerService.updateCustomer(eq(1L), any(CustomerRequest.class)))
                .willReturn(new CustomerResponse(1L, "Jane", "Doe", "jane@example.com", null, Instant.parse("2026-06-07T10:15:30Z")));

        mockMvc.perform(put("/api/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "email": "jane@example.com",
                                  "phone": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.phone").value(Matchers.nullValue()));

        verify(customerService).updateCustomer(eq(1L), any(CustomerRequest.class));
    }
}
