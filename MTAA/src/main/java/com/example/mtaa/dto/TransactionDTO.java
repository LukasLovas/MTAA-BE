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
    private Long amount;

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
    private Integer categoryId;

    @JsonProperty("budget_id")
    private Integer budgetId;

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


    public TransactionDTO() {
    }

    public TransactionDTO(String label, Long amount, LocalDateTime timestamp, String transactionTypeEnum, Integer category_id, Integer budget_id, String frequencyEnum, String note, String filename) {
        this.label = label;
        this.amount = amount;
        this.timestamp = timestamp;
        this.transactionTypeEnum = transactionTypeEnum;
        this.categoryId = category_id;
        this.budgetId = budget_id;
        this.frequencyEnum = frequencyEnum;
        this.note = note;
        this.filename = filename;
    }
}
