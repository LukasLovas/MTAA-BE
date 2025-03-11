package com.example.mtaa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    private Integer amount;

    public Transaction() {
    }

    public Transaction(Long id, String label, Integer amount) {
        this.id = Transaction.this.id;
        this.label = label;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long transactionId) {
        this.id = transactionId;
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
