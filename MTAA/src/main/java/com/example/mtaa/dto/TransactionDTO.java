package com.example.mtaa.dto;

public class TransactionDTO {
    private int transactionId;
    private String label;
    private Integer amount;

    public TransactionDTO() {
    }

    public TransactionDTO(int transactionId, String label, Integer amount) {
        this.transactionId = transactionId;
        this.label = label;
        this.amount = amount;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
