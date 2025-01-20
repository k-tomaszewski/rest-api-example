package io.github.k_tomaszewski.fxservice.api;

import io.github.k_tomaszewski.fxservice.api.model.CustomProblemDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

@SuppressWarnings("serial")
public class AccountNotFoundException extends ErrorResponseException {

    public static final String DETAIL = "Account does not exist.";

    public AccountNotFoundException() {
        super(HttpStatus.NOT_FOUND, new CustomProblemDetails(HttpStatus.NOT_FOUND, DETAIL, "account-not-found"), null);
    }
}
