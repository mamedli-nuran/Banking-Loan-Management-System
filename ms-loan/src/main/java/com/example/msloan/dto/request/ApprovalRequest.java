package com.example.msloan.dto.request;

import jakarta.validation.constraints.NotNull;

public record ApprovalRequest(
        @NotNull Boolean approved
) {
}
