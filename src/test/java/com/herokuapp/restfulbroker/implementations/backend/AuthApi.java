package com.herokuapp.restfulbroker.implementations.backend;

import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import com.herokuapp.restfulbroker.models.auth.AuthRequestDto;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import static io.restassured.RestAssured.given;
import static com.herokuapp.restfulbroker.Services.AUTH;

public final class AuthApi {
    private AuthApi() {
    }

    private static final Config config = ConfigFactory.load("config.properties");
    private static final String USERNAME = config.getString("username");
    private static final String PASSWORD = config.getString("password");
    public static final String BASE_URL = config.getString("baseUrl");

    public static Response createToken() {
        return given().contentType(ContentType.JSON)
                .body(AuthRequestDto.builder()
                        .username(USERNAME)
                        .password(PASSWORD)
                        .build())
                .filter(new ResponseLoggingFilter())
                .when()
                .post(BASE_URL + AUTH);
    }
}
