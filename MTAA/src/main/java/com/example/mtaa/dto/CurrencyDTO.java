package com.example.mtaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonTypeName("CurrencyDTO")
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDTO {

    @NotNull
    @JsonProperty("currency_code")
    @Schema(
            description = "Currency code",
            allowableValues = {"BGN", "BRL", "CAD", "CHF", "CNY",
                "CZK", "DKK", "EUR", "GBP", "HKD", "HRK", "HUF", "IDR",
                "ILS", "INR", "ISK", "JPY", "KRW", "MXN", "MYR", "NOK",
                "NZD", "PHP", "PLN", "RON", "RUB", "SEK", "SGD", "THB",
                "TRY", "USD", "ZAR"}
    )
    private String currencyCode;
}
