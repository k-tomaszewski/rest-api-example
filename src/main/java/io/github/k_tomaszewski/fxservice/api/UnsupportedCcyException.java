package io.github.k_tomaszewski.fxservice.api;

import io.github.k_tomaszewski.fxservice.api.model.CustomProblemDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.util.Currency;
import java.util.Set;

@SuppressWarnings("serial")
public class UnsupportedCcyException extends ErrorResponseException {

    public UnsupportedCcyException(Set<Currency> ccyCodes) {
        super(HttpStatus.BAD_REQUEST, new CustomProblemDetails(HttpStatus.BAD_REQUEST,
                "Unsupported currency: %s".formatted(ccyCodes), "unsupported-currency"), null);
    }
}
