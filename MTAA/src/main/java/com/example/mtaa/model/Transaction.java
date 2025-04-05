package com.example.mtaa.model;

import com.example.mtaa.model.enums.FrequencyEnum;
import com.example.mtaa.model.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "label")
    private String label;

    @NotNull
    @Column(name = "amount")
    private Long amount;

    @NotNull
    @Column(name = "date_created")
    private LocalDateTime creationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionTypeEnum transactionTypeEnum;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private FrequencyEnum frequencyEnum;

    @Column(name = "note")
    private String note;

    @Column(name = "filename")
    private String filename;
}
