package io.github.k_tomaszewski.fxservice.api.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountOpeningData(
        @NotBlank @Size(min = 2) @Pattern(regexp = "[\\p{L}- ',.]+")
        String firstName,
        @NotBlank @Size(min = 2) @Pattern(regexp = "[\\p{L}- ',.]+")
        String lastName,
        @NotNull @PositiveOrZero @Digits(integer = Integer.MAX_VALUE, fraction = 2)
        BigDecimal plnBalance) {
}
