package io.github.k_tomaszewski.fxservice.nbp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class FxRatesProvider implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(FxRatesProvider.class);
    private static final ZoneId PL_ZONE = ZoneId.of("Europe/Warsaw");

    private final FxRatesClient client;
    private final long timeoutSeconds;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final AtomicReference<CompletableFuture<BidAskPrices>> usdPlnPricesFetchingRef = new AtomicReference<>();
    private volatile BidAskPrices lastUsdPlnPrices;

    public FxRatesProvider(FxRatesClient client, @Value("${nbp.read-timeout}") Duration timeout) {
        this.client = client;
        timeoutSeconds = timeout.toSeconds();
    }

    public BidAskPrices getUsdPlnPrices() {
        BidAskPrices prices = lastUsdPlnPrices;
        if (prices != null && prices.effectiveDate().equals(LocalDate.now(PL_ZONE))) {
            return prices;
        }
        try {
            return fetchUsdPlnPrices().get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("FX rate not available.", e);
        } catch (TimeoutException e) {
            throw new ErrorResponseException(HttpStatus.GATEWAY_TIMEOUT,
                    ProblemDetail.forStatusAndDetail(HttpStatus.GATEWAY_TIMEOUT, "Timeout when fetching FX rate"), e);
        }
    }

    private CompletableFuture<BidAskPrices> fetchUsdPlnPrices() {
        CompletableFuture<BidAskPrices> newCompletable = new CompletableFuture<>();
        CompletableFuture<BidAskPrices> existingCompletable = usdPlnPricesFetchingRef.compareAndExchange(null, newCompletable);
        if (existingCompletable != null) {
            return existingCompletable;
        }
        return CompletableFuture.supplyAsync(client::getCurrentUsdPlnFxRates, executor)
                .handle((bidAskPrices, throwable) -> {
                    if (bidAskPrices != null) {
                        lastUsdPlnPrices = bidAskPrices;
                        newCompletable.complete(bidAskPrices);
                    }
                    if (throwable != null) {
                        LOG.error("Fetching USD/PLN prices failed.", throwable);
                        newCompletable.completeExceptionally(throwable);
                    }
                    usdPlnPricesFetchingRef.set(null);
                    return bidAskPrices;
                });
    }


    @Override
    public void close() {
        executor.shutdownNow();
    }
}
