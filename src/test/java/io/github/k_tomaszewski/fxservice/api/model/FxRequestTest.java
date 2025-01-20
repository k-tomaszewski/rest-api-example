package io.github.k_tomaszewski.fxservice.api.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

public class FxRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldDetectTheSameCurrencyUsedAsSrcAndDst() {
        // given
        var fxRequest = new FxRequest(Currency.getInstance("PLN"), new BigDecimal("1.00"), Currency.getInstance("PLN"), null);

        // when
        Set<ConstraintViolation<FxRequest>> violations = validator.validate(fxRequest);

        // then
        Assertions.assertEquals(1, violations.size());
        var violation = violations.iterator().next();
        Assertions.assertEquals("The same currency used as source and destination.", violation.getMessage());
    }

    @Test
    void shouldDetectMissingAmounts() {
        // given
        var fxRequest = new FxRequest(Currency.getInstance("PLN"), null, Currency.getInstance("USD"), null);

        // when
        Set<ConstraintViolation<FxRequest>> violations = validator.validate(fxRequest);

        // then
        Assertions.assertEquals(1, violations.size());
        var violation = violations.iterator().next();
        Assertions.assertEquals("Only one amount can be defined: source or destination.", violation.getMessage());
    }
}
