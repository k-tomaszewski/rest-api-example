package io.github.k_tomaszewski.fxservice.api;

import io.github.k_tomaszewski.fxservice.api.model.AccountDetails;
import io.github.k_tomaszewski.fxservice.api.model.CustomProblemDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AccountControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldOpenAccountWithCorrectData() {
        // given
        Map<String, String> requestBody = Map.of("firstName", "Jan", "lastName", "Nowak", "plnBalance", "1000.23");

        // when
        var responseEntity = restTemplate.exchange("/accounts", HttpMethod.POST, new HttpEntity<>(requestBody), AccountDetails.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        var account = responseEntity.getBody();
        Assertions.assertEquals("Jan", account.firstName());
        Assertions.assertEquals("Nowak", account.lastName());
        Assertions.assertEquals(new BigDecimal("1000.23"), account.balance().get(Currency.getInstance("PLN")));
    }

    @ParameterizedTest
    @CsvSource({",Rambo,1.00", "John,,1.00", "John,Rambo,", "J,Rambo,1.00", "John,R,1.00", "John,Rambo,-1.00", "John1,Rambo,1.00",
            "John,Rambo,1.001"})
    void shouldRejectAccountOpenRequestMissingFirstname(String firstName, String lastName, String balance) {
        // given
        Map<String, String> requestBody = new HashMap<>();
        if (firstName != null) {
            requestBody.put("firstName", firstName);
        }
        if (lastName != null) {
            requestBody.put("lastName", lastName);
        }
        if (balance != null) {
            requestBody.put("plnBalance", balance);
        }

        // when
        var responseEntity = restTemplate.exchange("/accounts", HttpMethod.POST, new HttpEntity<>(requestBody), CustomProblemDetails.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody().getFieldErrors());
    }

    @Test
    void shouldReturn404WhenAccountDoesNotExist() {
        // when
        var responseEntity = restTemplate.exchange("/accounts/123456789", HttpMethod.GET, new HttpEntity<>(null),
                CustomProblemDetails.class);

        // then
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals(AccountNotFoundException.DETAIL, responseEntity.getBody().getDetail());
    }

    @Test
    void shouldFetchExistingAccount() {
        // given
        Map<String, String> requestBody = Map.of("firstName", "Stefan", "lastName", "Kowalski", "plnBalance", "2000.55");

        // when
        var responseEntity1 = restTemplate.exchange("/accounts", HttpMethod.POST, new HttpEntity<>(requestBody), AccountDetails.class);
        final long accountId = responseEntity1.getBody().id();
        var responseEntity2 = restTemplate.exchange("/accounts/" + accountId, HttpMethod.GET, new HttpEntity<>(null), AccountDetails.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        Assertions.assertEquals(responseEntity1.getBody(), responseEntity2.getBody());
    }

    @Test
    void shouldDetectGlobalErrors() {
        // given
        Map<String, String> requestBody = Map.of("srcCcy", "PLN", "dstCcy", "PLN");

        // when
        var responseEntity = restTemplate.exchange("/accounts/1/fx-transactions", HttpMethod.POST, new HttpEntity<>(requestBody),
                CustomProblemDetails.class);

        // then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody().getGlobalErrors());
        Assertions.assertEquals(Set.of("Only one amount can be defined: source or destination.",
                "The same currency used as source and destination."), Set.copyOf(responseEntity.getBody().getGlobalErrors()));
    }
}
