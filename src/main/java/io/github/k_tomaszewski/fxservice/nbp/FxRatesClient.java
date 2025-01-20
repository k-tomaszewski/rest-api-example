package io.github.k_tomaszewski.fxservice.nbp;

import org.springframework.web.client.RestClient;

public class FxRatesClient {

    private final RestClient restClient;

    public FxRatesClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public BidAskPrices getCurrentUsdPlnFxRates() {
        return restClient.get()
                .uri("/exchangerates/rates/c/USD/last/1/")
                .retrieve()
                .body(FxRatesWrapper.class)
                .rates()
                .getFirst();
    }
}
