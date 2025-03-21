package com.example.mtaa.model;

import com.example.mtaa.model.enums.IntervalEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "budget")
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "label")
    private String label;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "interval_value")
    private Integer intervalValue;

    @Column(name = "interval_enum")
    private IntervalEnum intervalEnum;
}
