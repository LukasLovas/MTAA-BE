package com.example.mtaa.model;

import com.example.mtaa.model.enums.IntervalEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "budget")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String label;

    private Double amount;

    @Column(name = "initial_amount")
    private double initialAmount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "interval_value")
    private Integer intervalValue;

    @Column(name = "interval_enum")
    private IntervalEnum intervalEnum;

    @Column(name = "last_reset_date")
    private LocalDate lastResetDate;
}
