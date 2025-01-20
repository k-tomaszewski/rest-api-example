package io.github.k_tomaszewski.fxservice.nbp;

import io.github.k_tomaszewski.fxservice.model.CcyPairPrices;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BidAskPrices(BigDecimal bid, BigDecimal ask, LocalDate effectiveDate) implements CcyPairPrices {

    @Override
    public BigDecimal getBidPrice() {
        return bid;
    }

    @Override
    public BigDecimal getAskPrice() {
        return ask;
    }
}
