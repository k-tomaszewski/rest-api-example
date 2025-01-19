package io.github.k_tomaszewski.fxservice.service;

import io.github.k_tomaszewski.fxservice.api.model.AccountOpeningData;
import io.github.k_tomaszewski.fxservice.db.AccountRepository;
import io.github.k_tomaszewski.fxservice.model.Account;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public long createAccount(AccountOpeningData data) {
        return repository.save(new Account(data)).getId();
    }

    public Optional<Account> findAccount(long accountId) {
        return repository.findById(accountId);
    }
}
