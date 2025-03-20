package com.example.mtaa.model;

import com.example.mtaa.model.enums.FrequencyEnum;
import com.example.mtaa.model.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "transaction")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "label")
    private String label;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "date_created")
    private LocalDateTime creationDate;

    @Column(name = "transaction_type")
    private TransactionTypeEnum transactionTypeEnum;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "budget_id")
    private Long budgetId;

    @Column(name = "frequency")
    private FrequencyEnum frequencyEnum;

    @Column(name = "note")
    private String note;

    @Column(name = "attachment_id")
    private Long attachmentId;
}
