package com.example.mtaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonTypeName("CategorySpendingDTO")
@NoArgsConstructor
@AllArgsConstructor
public class CategorySpendingDTO {
    @JsonProperty("category_name")
    private String categoryName;
    @JsonProperty("amount")
    private double amount;

}
