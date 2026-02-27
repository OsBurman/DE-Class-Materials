package com.exercise.banking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class AccountRequest {
    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal initialBalance;

    public AccountRequest() {
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
