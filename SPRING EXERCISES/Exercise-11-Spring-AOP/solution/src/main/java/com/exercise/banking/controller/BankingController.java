package com.exercise.banking.controller;

import com.exercise.banking.dto.*;
import com.exercise.banking.service.BankingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class BankingController {

    private final BankingService bankingService;

    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bankingService.createAccount(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(bankingService.getAccountById(id));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(bankingService.getAllAccounts());
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable Long id,
                                                    @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(bankingService.deposit(id, request));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable Long id,
                                                     @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(bankingService.withdraw(id, request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestParam Long fromAccountId,
                                                         @Valid @RequestBody TransactionRequest request) {
        bankingService.transfer(fromAccountId, request);
        return ResponseEntity.ok(Map.of("message", "Transfer successful"));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(bankingService.getTransactions(id));
    }
}
