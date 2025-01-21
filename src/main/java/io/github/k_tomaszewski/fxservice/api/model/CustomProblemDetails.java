package io.github.k_tomaszewski.fxservice.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Payload;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("this-escape")
public class CustomProblemDetails extends ProblemDetail {

    private List<String> globalErrors;
    private List<Map<String, String>> fieldErrors;

    public CustomProblemDetails(HttpStatusCode status, String detail, String errCode) {
        super(status.value());
        setDetail(detail);
        if (errCode != null) {
            setType(URI.create("tag:io.github.k_tomaszewski,2025:" + errCode));
        }
    }

    public void populateErrors(Errors errors) {
        if (errors.hasGlobalErrors()) {
            globalErrors = errors.getGlobalErrors().stream().map(ObjectError::getDefaultMessage).toList();
        }
        if (errors.hasFieldErrors()) {
            fieldErrors = errors.getFieldErrors().stream()
                    .filter(error -> !isGlobalError(error))
                    .map(fieldError -> Map.of(fieldError.getField(), fieldError.getDefaultMessage()))
                    .toList();

            var globalErrorStream = errors.getFieldErrors().stream()
                    .filter(CustomProblemDetails::isGlobalError)
                    .map(FieldError::getDefaultMessage);
            if (globalErrors != null) {
                globalErrorStream = Stream.concat(globalErrors.stream(), globalErrorStream);
            }
            globalErrors = globalErrorStream.toList();
        }
    }

    public List<String> getGlobalErrors() {
        return globalErrors;
    }

    public List<Map<String, String>> getFieldErrors() {
        return fieldErrors;
    }

    public void setGlobalErrors(List<String> globalErrors) {
        this.globalErrors = globalErrors;
    }

    public void setFieldErrors(List<Map<String, String>> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    private static boolean isGlobalError(FieldError error) {
        return error.contains(ConstraintViolation.class)
                && error.unwrap(ConstraintViolation.class).getConstraintDescriptor().getPayload().contains(GlobalError.class);
    }

    public static class GlobalError implements Payload { };
}
