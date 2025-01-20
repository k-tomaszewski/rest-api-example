package io.github.k_tomaszewski.fxservice.api;

import io.github.k_tomaszewski.fxservice.api.model.AccountDetails;
import io.github.k_tomaszewski.fxservice.api.model.AccountOpeningData;
import io.github.k_tomaszewski.fxservice.api.model.FxRequest;
import io.github.k_tomaszewski.fxservice.api.model.FxSummary;
import io.github.k_tomaszewski.fxservice.model.Account;
import io.github.k_tomaszewski.fxservice.service.AccountService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import static io.github.k_tomaszewski.fxservice.model.Account.ZERO_AMOUNT;
import static io.github.k_tomaszewski.fxservice.service.AccountService.PLN;
import static io.github.k_tomaszewski.fxservice.service.AccountService.USD;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountDetails createAccount(@RequestBody @Validated AccountOpeningData data) {
        long accountId = service.createAccount(data);
        return new AccountDetails(accountId, data.firstName(), data.lastName(), Map.of(PLN, data.plnBalance(), USD, ZERO_AMOUNT));
    }

    @GetMapping("/{id}")
    public AccountDetails getAccount(@PathVariable("id") long accountId) {
        return service.findAccount(accountId)
                .map(account -> new AccountDetails(accountId, account.getFirstName(), account.getLastName(), toBalanceMap(account)))
                .orElseThrow(AccountNotFoundException::new);
    }

    @PostMapping("/{id}/fx-transactions")
    public FxSummary exchange(@PathVariable("id") long accountId, @RequestBody @Validated FxRequest fxRequest) {
        return service.exchange(accountId, fxRequest)
                .orElseThrow(AccountNotFoundException::new);
    }

    private static Map<Currency, BigDecimal> toBalanceMap(Account account) {
        return Map.of(PLN, account.getPlnBalance(), USD, account.getUsdBalance());
    }
}
