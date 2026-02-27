package com.exercise.banking.service;

import com.exercise.banking.annotation.Audited;
import com.exercise.banking.dto.*;
import com.exercise.banking.entity.Account;
import com.exercise.banking.entity.Transaction;
import com.exercise.banking.exception.InsufficientFundsException;
import com.exercise.banking.exception.ResourceNotFoundException;
import com.exercise.banking.repository.AccountRepository;
import com.exercise.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BankingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BankingService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public AccountResponse createAccount(AccountRequest request) {
        Account account = new Account();
        account.setAccountNumber("ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        account.setOwnerName(request.getOwnerName());
        account.setBalance(request.getInitialBalance());
        return toAccountResponse(accountRepository.save(account));
    }

    public AccountResponse getAccountById(Long id) {
        return toAccountResponse(accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id)));
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream().map(this::toAccountResponse).collect(Collectors.toList());
    }

    // TODO 10: Add @Audited(action = "DEPOSIT") annotation above this method.
    // When deposit() is called, the AuditAspect will intercept it
    // and log the audit entry because of this annotation.
    @Transactional
    public AccountResponse deposit(Long accountId, TransactionRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);
        recordTransaction(accountId, "DEPOSIT", request.getAmount(), request.getDescription());
        return toAccountResponse(account);
    }

    // TODO 11: Add @Audited(action = "WITHDRAW") annotation above this method.
    @Transactional
    public AccountResponse withdraw(Long accountId, TransactionRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Balance: " + account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);
        recordTransaction(accountId, "WITHDRAWAL", request.getAmount(), request.getDescription());
        return toAccountResponse(account);
    }

    // TODO 12: Add @Audited(action = "TRANSFER") annotation above this method.
    @Transactional
    public void transfer(Long fromAccountId, TransactionRequest request) {
        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", fromAccountId));
        Account to = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", request.getToAccountId()));
        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }
        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);
        recordTransaction(fromAccountId, "TRANSFER_OUT", request.getAmount(),
                "Transfer to account " + to.getAccountNumber());
        recordTransaction(to.getId(), "TRANSFER_IN", request.getAmount(),
                "Transfer from account " + from.getAccountNumber());
    }

    public List<TransactionResponse> getTransactions(Long accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(this::toTransactionResponse).collect(Collectors.toList());
    }

    private void recordTransaction(Long accountId, String type, BigDecimal amount, String description) {
        Transaction t = new Transaction();
        t.setAccountId(accountId);
        t.setType(type);
        t.setAmount(amount);
        t.setDescription(description);
        transactionRepository.save(t);
    }

    private AccountResponse toAccountResponse(Account a) {
        AccountResponse r = new AccountResponse();
        r.setId(a.getId());
        r.setAccountNumber(a.getAccountNumber());
        r.setOwnerName(a.getOwnerName());
        r.setBalance(a.getBalance());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }

    private TransactionResponse toTransactionResponse(Transaction t) {
        TransactionResponse r = new TransactionResponse();
        r.setId(t.getId());
        r.setAccountId(t.getAccountId());
        r.setType(t.getType());
        r.setAmount(t.getAmount());
        r.setDescription(t.getDescription());
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }
}
