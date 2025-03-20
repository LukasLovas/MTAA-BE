package com.example.mtaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonTypeName("TransactionDTO")
public class TransactionDTO {
    @JsonProperty("label")
    private String label;
    @JsonProperty("amount")
    private Long amount;
    @JsonProperty("time")
    private LocalDateTime timestamp;
    @JsonProperty("transaction_type")
    private String transactionTypeEnum;
    @JsonProperty("category_id")
    private Long categoryId;
    @JsonProperty("budget_id")
    private Long budgetId;
    @JsonProperty("frequency")
    private String frequencyEnum;
    @JsonProperty("note")
    private String note;
    @JsonProperty("attachment_id")
    private Long attachmentId;


    public TransactionDTO() {
    }

    public TransactionDTO(String label, Long amount, LocalDateTime timestamp, String transactionTypeEnum, Long category_id, Long budget_id, String frequencyEnum, String note, Long attachment_id) {
        this.label = label;
        this.amount = amount;
        this.timestamp = timestamp;
        this.transactionTypeEnum = transactionTypeEnum;
        this.categoryId = category_id;
        this.budgetId = budget_id;
        this.frequencyEnum = frequencyEnum;
        this.note = note;
        this.attachmentId = attachment_id;
    }
}
