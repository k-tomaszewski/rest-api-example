package io.github.k_tomaszewski.fxservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.util.Currency;
import java.util.Set;

@SuppressWarnings("serial")
public class UnsupportedCcyException extends ErrorResponseException {

    public UnsupportedCcyException(Set<Currency> ccyCodes) {
        super(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Unsupported currency: %s".formatted(ccyCodes)), null);
    }
}
