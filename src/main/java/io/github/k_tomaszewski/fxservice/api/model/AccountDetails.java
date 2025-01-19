package io.github.k_tomaszewski.fxservice.api.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

public record AccountDetails(long id, String firstName, String lastName, Map<Currency, BigDecimal> balance) {
}
