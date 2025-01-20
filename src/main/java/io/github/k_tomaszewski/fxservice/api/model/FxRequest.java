package io.github.k_tomaszewski.fxservice.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.k_tomaszewski.fxservice.api.model.CustomProblemDetails.GlobalError;
import jakarta.validation.constraints.AssertFalse;
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

        @AssertTrue(message = "Only one amount can be defined: source or destination.", payload = GlobalError.class)
        @JsonIgnore
        public boolean isOnlyOneAmountDefined() {
                return (srcAmount != null) ^ (dstAmount != null);
        }

        @AssertFalse(message = "The same currency used as source and destination.", payload = GlobalError.class)
        @JsonIgnore
        public boolean isTheSameCurrencyUsedAsSrcAndDst() {
                return Objects.equals(srcCcy, dstCcy);
        }
}
