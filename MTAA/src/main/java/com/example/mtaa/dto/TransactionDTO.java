package com.example.mtaa.dto;

import lombok.Data;

@Data
public class TransactionDTO {
    private String label;
    private Long amount;

    public TransactionDTO() {
    }

    public TransactionDTO(String label, Long amount) {
        this.label = label;
        this.amount = amount;
    }
}
