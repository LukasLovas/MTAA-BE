package com.example.mtaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@Data
@JsonTypeName("CategoryDTO")
public class CategoryDTO {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("label")
    private String label;

    public CategoryDTO() {
    }

    public CategoryDTO(Long userId, String label) {
        this.userId = userId;
        this.label = label;
    }
}
