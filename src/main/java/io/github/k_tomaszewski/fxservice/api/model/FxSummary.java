package io.github.k_tomaszewski.fxservice.api.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

public record FxSummary(Currency srcCcy, BigDecimal srcAmount, Currency dstCcy, BigDecimal dstAmount, BigDecimal price,
                        Map<Currency, BigDecimal> accountBalance) {
}
