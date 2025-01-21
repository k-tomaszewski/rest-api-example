package io.github.k_tomaszewski.fxservice.nbp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FxRatesWrapperTest {

    @Test
    void shouldFitToDeserializationOfNbpResponseBody() throws IOException {
        // given
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        FxRatesWrapper fxRatesWrapper;
        try (var reader = new InputStreamReader(getClass().getResourceAsStream("/nbp_table_c_last_1.json"), UTF_8)) {
            // when
            fxRatesWrapper = objectMapper.readValue(reader, FxRatesWrapper.class);
        }

        // then
        Assertions.assertEquals(1, fxRatesWrapper.rates().size());
        BidAskPrices prices = fxRatesWrapper.rates().getFirst();
        Assertions.assertEquals(LocalDate.parse("2025-01-20"), prices.effectiveDate());
        Assertions.assertEquals(new BigDecimal("4.0914"), prices.bid());
        Assertions.assertEquals(new BigDecimal("4.1740"), prices.ask());
    }
}
