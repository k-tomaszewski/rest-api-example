package io.github.k_tomaszewski.fxservice.service;

import io.github.k_tomaszewski.fxservice.api.UnsufficientFundsException;
import io.github.k_tomaszewski.fxservice.api.UnsupportedCcyException;
import io.github.k_tomaszewski.fxservice.api.model.AccountOpeningData;
import io.github.k_tomaszewski.fxservice.api.model.FxRequest;
import io.github.k_tomaszewski.fxservice.api.model.FxSummary;
import io.github.k_tomaszewski.fxservice.db.AccountRepository;
import io.github.k_tomaszewski.fxservice.model.Account;
import io.github.k_tomaszewski.fxservice.model.CcyPairPrices;
import io.github.k_tomaszewski.fxservice.nbp.FxRatesProvider;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;

import static java.math.RoundingMode.HALF_UP;

@Service
public class AccountService {

    public static final Currency PLN = Currency.getInstance("PLN");
    public static final Currency USD = Currency.getInstance("USD");
    public static final int SCALE = 2;

    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);
    private static final Set<Currency> SUPPORTED_CCY_SET = Set.of(PLN, USD);

    private final AccountRepository repository;
    private final FxRatesProvider fxRatesProvider;

    public AccountService(AccountRepository repository, FxRatesProvider fxRatesProvider) {
        this.repository = repository;
        this.fxRatesProvider = fxRatesProvider;
    }

    public long createAccount(AccountOpeningData data) {
        var accountId = repository.save(new Account(data)).getId();
        LOG.info("Created account with id = {}", accountId);
        return accountId;
    }

    public Optional<Account> findAccount(long accountId) {
        return repository.findById(accountId);
    }

    @Transactional
    public Optional<FxSummary> exchange(long accountId, FxRequest fxRequest) {
        validateCurrencySupport(fxRequest);
        return findAccount(accountId).map(account -> exchange(account, fxRequest, fxRatesProvider.getUsdPlnPrices()));
    }

    static FxSummary exchange(Account account, FxRequest fxRequest, CcyPairPrices prices) {
        final BigDecimal srcAmount;
        if (fxRequest.srcAmount() != null) {
            srcAmount = fxRequest.srcAmount();
        } else {
            if (fxRequest.dstCcy() == PLN) {
                srcAmount = fxRequest.dstAmount().divide(prices.getBidPrice(), SCALE, HALF_UP);
            } else {
                // dstCcy == USD
                srcAmount = fxRequest.dstAmount().multiply(prices.getAskPrice()).setScale(SCALE, HALF_UP);
            }
        }
        if (getBalance(account, fxRequest.srcCcy()).compareTo(srcAmount) < 0) {
            throw new UnsufficientFundsException(fxRequest.srcCcy(), srcAmount);
        }

        final BigDecimal dstAmount;
        final BigDecimal price;
        if (fxRequest.srcCcy() == PLN) {
            price = prices.getAskPrice();
            dstAmount = srcAmount.divide(price, SCALE, HALF_UP);
        } else {
            price = prices.getBidPrice();
            dstAmount = srcAmount.multiply(price).setScale(SCALE, HALF_UP);
        }

        updateBalance(account, fxRequest.srcCcy(), BigDecimal::subtract, srcAmount);
        updateBalance(account, fxRequest.dstCcy(), BigDecimal::add, dstAmount);

        return new FxSummary(fxRequest.srcCcy(), srcAmount, fxRequest.dstCcy(), dstAmount, price,
                Map.of(PLN, account.getPlnBalance(), USD, account.getUsdBalance()));
    }

    // These validations cannot be handled easy by annotation-driven Java Bean Validation because they require knowledge
    // about supported currencies. This knowledge should be provided by a service layer of the application.
    static void validateCurrencySupport(FxRequest fxRequest) {
        Set<Currency> unsupportedCcy = new HashSet<>();
        if (!SUPPORTED_CCY_SET.contains(fxRequest.srcCcy())) {
            unsupportedCcy.add(fxRequest.srcCcy());
        }
        if (!SUPPORTED_CCY_SET.contains(fxRequest.dstCcy())) {
            unsupportedCcy.add(fxRequest.dstCcy());
        }
        if (!unsupportedCcy.isEmpty()) {
            throw new UnsupportedCcyException(unsupportedCcy);
        }
    }

    private static BigDecimal getBalance(Account account, Currency ccy) {
        return PLN.equals(ccy) ? account.getPlnBalance() : account.getUsdBalance();
    }

    private static void updateBalance(Account account, Currency ccy, BinaryOperator<BigDecimal> operator, BigDecimal operand) {
        var newBalance = operator.apply(getBalance(account, ccy), operand);
        if (PLN.equals(ccy)) {
            account.setPlnBalance(newBalance);
        } else {
            account.setUsdBalance(newBalance);
        }
    }
}
