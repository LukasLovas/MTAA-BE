package com.example.mtaa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    private Long amount;

    public Transaction() {
    }

    public Transaction(String label, Long amount) {
        this.label = label;
        this.amount = amount;
    }
}
