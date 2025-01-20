package io.github.k_tomaszewski.fxservice.api.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record FxRequest(
        @NotNull
        Currency srcCcy,
        @Positive
        BigDecimal srcAmount,
        @NotNull
        Currency dstCcy,
        @Positive
        BigDecimal dstAmount) {

        @AssertTrue
        public boolean onlyOneAmountDefined() {
                return (srcAmount != null) ^ (dstAmount != null);
        }

        @AssertTrue
        public boolean differentCurrencies() {
                return !Objects.equals(srcCcy, dstCcy);
        }
}
