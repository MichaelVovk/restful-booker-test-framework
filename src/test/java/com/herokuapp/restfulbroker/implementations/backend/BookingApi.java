package com.herokuapp.restfulbroker.implementations.backend;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.herokuapp.restfulbroker.models.booking.BookingRequestDto;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static com.herokuapp.restfulbroker.Services.BOOKING;

public final class BookingApi {
    private static final String MEDIA_TYPE_JSON = "application/json";

    private RequestSpecification send() {
        return given(new RequestSpecBuilder()
                .setBaseUri(AuthApi.BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .setBasePath(BOOKING.getService())
                .addHeader("Accept", BookingApi.MEDIA_TYPE_JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build());
    }
    @Step("get booking by id")
    public Response getBookingById(int id) {
        return send().get("/" + id);
    }
    @Step("create booking")
    public Response createBooking(BookingRequestDto bookingRequestDto) {
        return send().body(bookingRequestDto).post();
    }
    @Step("update booking")
    public Response partialUpdateBooking(BookingRequestDto bookingRequestDto, int id, String tokenValue) {
        return send().header("Cookie", "token=" + tokenValue).body(bookingRequestDto).patch("/" + id);
    }

    @Step("get bookings")
    public Response getBookings(Map<String, ?> params) {
        return send().params(params).get();
    }

    @Step("delete booking")
    public Response deleteBooking(int id, String authToken) {
        return send().header("Cookie", "token=" + authToken)
                .delete("/" + id);
    }
}
