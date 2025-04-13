package com.example.mtaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonTypeName("LocationDTO")
public class LocationDTO {

    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("latitude")
    private BigDecimal latitude;

    @NotNull
    @JsonProperty("longitude")
    private BigDecimal longitude;
}
