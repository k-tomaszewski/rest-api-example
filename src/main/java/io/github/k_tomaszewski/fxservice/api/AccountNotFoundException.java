package io.github.k_tomaszewski.fxservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class AccountNotFoundException extends ErrorResponseException {

    public static final String DETAIL = "Account does not exist.";

    public AccountNotFoundException() {
        super(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, DETAIL), null);
    }
}
