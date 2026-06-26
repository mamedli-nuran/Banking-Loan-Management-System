package com.example.msloan.controller;

import com.example.msloan.dto.request.ApprovalRequest;
import com.example.msloan.dto.request.LoanRequest;
import com.example.msloan.dto.response.LoanResponse;
import com.example.msloan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> apply(@Valid @RequestBody LoanRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(loanService.apply(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loanService.getById(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<LoanResponse> approve(@PathVariable Long id, @Valid @RequestBody ApprovalRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loanService.approve(id, request));
    }

    @PutMapping("/{id}/disburse")
    public ResponseEntity<LoanResponse> disburse(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loanService.disburse(id));
    }
}
