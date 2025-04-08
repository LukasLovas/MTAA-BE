package com.example.mtaa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonTypeName("UserDTO")
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @JsonProperty("username")
    @NotNull
    private String username;

    @JsonProperty("password")
    @NotNull
    private String password;
}
