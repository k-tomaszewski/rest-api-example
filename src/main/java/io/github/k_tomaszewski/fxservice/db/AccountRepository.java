package io.github.k_tomaszewski.fxservice.db;

import io.github.k_tomaszewski.fxservice.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
}
