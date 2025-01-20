package io.github.k_tomaszewski.fxservice.service;

import io.github.k_tomaszewski.fxservice.api.UnsufficientFundsException;
import io.github.k_tomaszewski.fxservice.api.UnsupportedCcyException;
import io.github.k_tomaszewski.fxservice.api.model.FxRequest;
import io.github.k_tomaszewski.fxservice.model.Account;
import io.github.k_tomaszewski.fxservice.model.CcyPairPrices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.util.Currency;

import static io.github.k_tomaszewski.fxservice.service.AccountService.PLN;
import static io.github.k_tomaszewski.fxservice.service.AccountService.USD;

public class AccountServiceTest {

    private static final CcyPairPrices PRICES = new CcyPairPrices() {
        @Override
        public BigDecimal getBidPrice() {
            return new BigDecimal("4.0914");
        }
        @Override
        public BigDecimal getAskPrice() {
            return new BigDecimal("4.1740");
        }
    };

    @Test
    void shouldExchangeGivenPlnAmountToUsd() {
        // given
        var account = new Account();
        account.setPlnBalance(new BigDecimal("12.00"));
        account.setUsdBalance(new BigDecimal("1.00"));

        var fxRequest = new FxRequest(PLN, new BigDecimal("10.00"), USD, null);

        // when
        var summary = AccountService.exchange(account, fxRequest, PRICES);

        // then
        Assertions.assertEquals(PLN, summary.srcCcy());
        Assertions.assertEquals(USD, summary.dstCcy());
        Assertions.assertEquals(new BigDecimal("10.00"), summary.srcAmount());
        Assertions.assertEquals(new BigDecimal("2.40"), summary.dstAmount());
        Assertions.assertEquals(PRICES.getAskPrice(), summary.price());
        Assertions.assertEquals(new BigDecimal("2.00"), summary.accountBalance().get(PLN));
        Assertions.assertEquals(new BigDecimal("3.40"), summary.accountBalance().get(USD));
    }

    @Test
    void shouldExchangePlnForAtLeastGivenUsdAmount() {
        // given
        var account = new Account();
        account.setPlnBalance(new BigDecimal("12.00"));
        account.setUsdBalance(new BigDecimal("1.00"));

        var fxRequest = new FxRequest(PLN, null, USD, new BigDecimal("2.50"));

        // when
        var summary = AccountService.exchange(account, fxRequest, PRICES);

        // then
        Assertions.assertEquals(PLN, summary.srcCcy());
        Assertions.assertEquals(USD, summary.dstCcy());
        Assertions.assertEquals(new BigDecimal("10.44"), summary.srcAmount());
        Assertions.assertEquals(new BigDecimal("2.50"), summary.dstAmount());
        Assertions.assertEquals(PRICES.getAskPrice(), summary.price());
        Assertions.assertEquals(new BigDecimal("1.56"), summary.accountBalance().get(PLN));
        Assertions.assertEquals(new BigDecimal("3.50"), summary.accountBalance().get(USD));
    }

    @Test
    void shouldExchangeGivenUsdAmountToPln() {
        // given
        var account = new Account();
        account.setPlnBalance(new BigDecimal("12.00"));
        account.setUsdBalance(new BigDecimal("5.00"));

        var fxRequest = new FxRequest(USD, new BigDecimal("3.00"), PLN, null);

        // when
        var summary = AccountService.exchange(account, fxRequest, PRICES);

        // then
        Assertions.assertEquals(USD, summary.srcCcy());
        Assertions.assertEquals(PLN, summary.dstCcy());
        Assertions.assertEquals(new BigDecimal("3.00"), summary.srcAmount());
        Assertions.assertEquals(new BigDecimal("12.27"), summary.dstAmount());
        Assertions.assertEquals(PRICES.getBidPrice(), summary.price());
        Assertions.assertEquals(new BigDecimal("24.27"), summary.accountBalance().get(PLN));
        Assertions.assertEquals(new BigDecimal("2.00"), summary.accountBalance().get(USD));
    }

    @Test
    void shouldExchangeUsdToAtLeastGivenPlnAmount() {
        // given
        var account = new Account();
        account.setPlnBalance(new BigDecimal("12.00"));
        account.setUsdBalance(new BigDecimal("5.00"));

        var fxRequest = new FxRequest(USD, null, PLN, new BigDecimal("2.99"));

        // when
        var summary = AccountService.exchange(account, fxRequest, PRICES);

        // thrn
        Assertions.assertEquals(USD, summary.srcCcy());
        Assertions.assertEquals(PLN, summary.dstCcy());
        Assertions.assertEquals(new BigDecimal("0.73"), summary.srcAmount());
        Assertions.assertEquals(new BigDecimal("2.99"), summary.dstAmount());
        Assertions.assertEquals(PRICES.getBidPrice(), summary.price());
        Assertions.assertEquals(new BigDecimal("14.99"), summary.accountBalance().get(PLN));
        Assertions.assertEquals(new BigDecimal("4.27"), summary.accountBalance().get(USD));
    }

    @Test
    void shouldDetectNotEnoughSrcCcyAmount() {
        // given
        var account = new Account();
        account.setPlnBalance(new BigDecimal("12.00"));
        account.setUsdBalance(new BigDecimal("0.00"));

        var fxRequest = new FxRequest(PLN, null, USD, new BigDecimal("10.00"));

        // when
        Executable testCase = () -> AccountService.exchange(account, fxRequest, PRICES);

        // then
        Assertions.assertThrows(UnsufficientFundsException.class, testCase);
    }

    @Test
    void shouldDetectNotSupportedCurrencies() {
        // given
        var fxRequest = new FxRequest(Currency.getInstance("EUR"), null, Currency.getInstance("DKK"), new BigDecimal("10.00"));

        // when
        Executable testCase = () -> AccountService.validateCurrencySupport(fxRequest);

        // then
        Assertions.assertThrows(UnsupportedCcyException.class, testCase);
    }
}
