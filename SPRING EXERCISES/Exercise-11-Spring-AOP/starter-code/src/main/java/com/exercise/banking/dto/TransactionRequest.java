package com.exercise.banking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TransactionRequest {
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    private Long toAccountId; // for transfers only

    private String description;

    public TransactionRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
