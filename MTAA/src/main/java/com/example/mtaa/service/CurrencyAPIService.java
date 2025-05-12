package com.example.mtaa.service;

import com.example.mtaa.dto.CurrencyResponse;
import com.example.mtaa.dto.TransactionDTO;
import com.example.mtaa.model.CommonException;
import com.example.mtaa.model.User;
import com.example.mtaa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class CurrencyAPIService {

    @Value("${api.key}")
    private String apiKey;

    private final UserService userService;
    private final WebClient webClient;

    public CurrencyAPIService(UserService userService, WebClient webClient) {
        this.userService = userService;
        this.webClient = webClient;
    }

    public TransactionDTO convertCurrency(TransactionDTO transactionDTO) {
        User user = userService.findCurrentUser();
        String targetCurrency = user.getCurrency().name();
        String baseCurrency = transactionDTO.getCurrencyCode();

        if (!baseCurrency.equals(targetCurrency)) {
            CurrencyResponse currencyResponse = getExchangeRate().block();

            if (currencyResponse == null || currencyResponse.getData() == null) {
                throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve exchange rate");
            }

            if (baseCurrency.equals("USD")) {
                transactionDTO.setAmount(transactionDTO.getAmount() * currencyResponse.getData().get(targetCurrency));
            } else if (targetCurrency.equals("USD")) {
                transactionDTO.setAmount(transactionDTO.getAmount() / currencyResponse.getData().get(baseCurrency));
            } else {
                transactionDTO.setAmount(transactionDTO.getAmount() * (currencyResponse.getData().get(targetCurrency) / currencyResponse.getData().get(baseCurrency)));
            }
        }

        return transactionDTO;
    }

public Mono<CurrencyResponse> getExchangeRate() {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/latest")
                    .queryParam("apikey", apiKey)
                    .build())
            .retrieve()
            .bodyToMono(CurrencyResponse.class);
}

}
