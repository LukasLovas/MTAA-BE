package com.example.mtaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class CurrencyResponse {

    @JsonProperty("data")
    private Map<String, Double> data;

}
