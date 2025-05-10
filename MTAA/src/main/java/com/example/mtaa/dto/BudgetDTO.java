package com.example.mtaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonTypeName("BudgetDTO")
public class BudgetDTO {

    @JsonProperty("label")
    private String label;
    @JsonProperty("amount")
    private double amount;
    @JsonProperty("start_date")
    private LocalDateTime startDate;
    @JsonProperty("interval_value")
    private Integer intervalValue;
    @JsonProperty("interval_enum")
    @Schema(
            description = "Interval - ENUM values",
            allowableValues = {"DAY", "WEEK", "MONTH", "YEAR"}
    )
    private String intervalEnum;

    public BudgetDTO() {
    }

    public BudgetDTO(String label, double amount, LocalDateTime startDate, Integer intervalValue, String intervalEnum) {
        this.label = label;
        this.amount = amount;
        this.startDate = startDate;
        this.intervalValue = intervalValue;
        this.intervalEnum = intervalEnum;
    }
}
