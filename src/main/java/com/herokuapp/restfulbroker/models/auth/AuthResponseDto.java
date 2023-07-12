package com.herokuapp.restfulbroker.models.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public class AuthResponseDto {
    @JsonProperty("token")
    private String token;
}
