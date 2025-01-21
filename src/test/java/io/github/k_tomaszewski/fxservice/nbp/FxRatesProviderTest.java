package io.github.k_tomaszewski.fxservice.nbp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FxRatesProviderTest {

    private static final Logger LOG = LoggerFactory.getLogger(FxRatesProviderTest.class);

    @Test
    void shouldFetchPricesOncePerDay() {
        // given
        final var currentDay = LocalDate.now(FxRatesProvider.PL_ZONE);

        var clientMock = Mockito.mock(FxRatesClient.class);
        Mockito.when(clientMock.getCurrentUsdPlnFxRates()).then(invocationOnMock -> {
            sleep(1000);
            return new BidAskPrices(BigDecimal.ONE, BigDecimal.TWO, currentDay);
        });

        var provider = new FxRatesProvider(clientMock, Duration.ofSeconds(5));

        // when
        var prices = provider.getUsdPlnPrices();
        var prices2 = provider.getUsdPlnPrices();
        provider.close();

        // then
        Mockito.verify(clientMock, Mockito.times(1)).getCurrentUsdPlnFxRates();
        Assertions.assertEquals(BigDecimal.ONE, prices.bid());
        Assertions.assertEquals(BigDecimal.TWO, prices.ask());
        Assertions.assertEquals(currentDay, prices.effectiveDate());
        Assertions.assertEquals(prices, prices2);
    }

    @Test
    void shouldFetchPricesOnceWhenCalledConcurrently() throws ExecutionException, InterruptedException {
        // given
        final var currentDay = LocalDate.now(FxRatesProvider.PL_ZONE);

        var clientMock = Mockito.mock(FxRatesClient.class);
        Mockito.when(clientMock.getCurrentUsdPlnFxRates()).then(invocationOnMock -> {
            sleep(1000);
            return new BidAskPrices(BigDecimal.ONE, BigDecimal.TWO, currentDay);
        });

        var provider = new FxRatesProvider(clientMock, Duration.ofSeconds(5));

        // when
        List<CompletableFuture<BidAskPrices>> completables = new ArrayList<>(10);
        for (int i = 0; i < 10; ++i) {
            completables.add(CompletableFuture.supplyAsync(() -> {
                LOG.info("Fetching prices...");
                var prices = provider.getUsdPlnPrices();
                LOG.info("Fetching completed.");
                return prices;
            }));
        }
        completables.forEach(CompletableFuture::join);
        provider.close();

        // then
        Mockito.verify(clientMock, Mockito.times(1)).getCurrentUsdPlnFxRates();

        var prices = completables.getFirst().get();
        Assertions.assertEquals(BigDecimal.ONE, prices.bid());
        Assertions.assertEquals(BigDecimal.TWO, prices.ask());
        Assertions.assertEquals(currentDay, prices.effectiveDate());

        for (int i = 1; i < 10; ++i) {
            var otherPrices = completables.get(i).get();
            Assertions.assertEquals(prices, otherPrices, "Prices fetched by thread %d are different".formatted(i));
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException("Sleeping interrupted", e);
        }
    }
}
