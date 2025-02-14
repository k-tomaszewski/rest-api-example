package io.github.k_tomaszewski.fxservice.api;

import io.github.k_tomaszewski.fxservice.api.model.CustomProblemDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.math.BigDecimal;
import java.util.Currency;

@SuppressWarnings("serial")
public class UnsufficientFundsException extends ErrorResponseException {

    public UnsufficientFundsException(Currency ccy, BigDecimal neededAmount) {
        super(HttpStatus.CONFLICT, new CustomProblemDetails(HttpStatus.CONFLICT,
                "Your balance in %s is below %s.".formatted(ccy, neededAmount), "unsufficient-funds"), null);
    }
}
