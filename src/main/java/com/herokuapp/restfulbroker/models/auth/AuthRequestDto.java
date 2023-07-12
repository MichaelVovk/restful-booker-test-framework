package com.herokuapp.restfulbroker.models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public class AuthRequestDto {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;
}
