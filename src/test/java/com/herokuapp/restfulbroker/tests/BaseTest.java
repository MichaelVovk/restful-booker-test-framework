package com.herokuapp.restfulbroker.tests;

import io.restassured.response.Response;
import net.datafaker.Faker;
import com.herokuapp.restfulbroker.implementations.backend.AuthApi;
import com.herokuapp.restfulbroker.models.auth.AuthResponseDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseTest {
    protected final Faker faker = new Faker();

    protected String token;

    @BeforeEach
    void testCreateTokenReturns200() {
        Response response = AuthApi.createToken();
        token = response.as(AuthResponseDto.class).getToken();
        assertThat(response.statusCode(), equalTo(SC_OK));
    }

    @Test
    void testCreateTokenReturnsNonEmptyToken() {
        assertThat(token, is(not(emptyString())));
    }
}
