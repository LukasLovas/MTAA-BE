package com.example.mtaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonTypeName("TransactionDTO")
public class TransactionDTO {

    @NotNull
    @JsonProperty("user_id")
    private Long userId;

    @NotNull
    @JsonProperty("label")
    private String label;

    @NotNull
    @JsonProperty("amount")
    private double amount;

    @NotNull
    @JsonProperty("time")
    private LocalDateTime timestamp;

    @NotNull
    @JsonProperty("transaction_type")
    @Schema(
            description = "Transaction Type - ENUM values",
            allowableValues = {"EXPENSE", "INCOME"}
    )
    private String transactionTypeEnum;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("budget_id")
    private Long budgetId;

    @JsonProperty("location_id")
    private Long locationId;

    @NotNull
    @JsonProperty("frequency")
    @Schema(
            description = "Frequency Type - ENUM values",
            allowableValues = {"DEFAULT", "UPCOMING", "SUBSCRIPTION"}
    )
    private String frequencyEnum;

    @JsonProperty("note")
    private String note;

    @JsonProperty("filename")
    private String filename;

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

    public TransactionDTO() {
    }

    public TransactionDTO(String label, double amount, LocalDateTime timestamp, String transactionTypeEnum, Long category_id, Long budget_id, String frequencyEnum, String note, String filename, String currencyCode) {
        this.label = label;
        this.amount = amount;
        this.timestamp = timestamp;
        this.transactionTypeEnum = transactionTypeEnum;
        this.categoryId = category_id;
        this.budgetId = budget_id;
        this.frequencyEnum = frequencyEnum;
        this.note = note;
        this.filename = filename;
        this.currencyCode = currencyCode;
    }
}
