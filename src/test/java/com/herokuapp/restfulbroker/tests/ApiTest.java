package com.herokuapp.restfulbroker.tests;

import com.herokuapp.restfulbroker.models.booking.BookingDatesDto;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import com.herokuapp.restfulbroker.implementations.backend.BookingApi;
import com.herokuapp.restfulbroker.models.booking.BookingRequestDto;
import com.herokuapp.restfulbroker.models.booking.BookingResponseDto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.http.HttpStatus.*;

class ApiTest extends BaseTest {

    private static final String TOKEN = "token";
    private static final String CHECKIN = "checkin";
    private static final String CHECKOUT = "checkout";
    private static final String LASTNAME = "lastname";
    private static final String FIRSTNAME = "firstname";
    BookingApi bookingApi = new BookingApi();

    @Step
    BookingRequestDto createBookingRequestDto() {
        return BookingRequestDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .totalPrice(faker.number().positive())
                .depositPaid(true)
                .bookingDatesDto(
                        BookingDatesDto.builder()
                                .checkIn(faker.date().past(1, TimeUnit.DAYS, "yyyy-MM-dd"))
                                .checkOut(faker.date().past(1, TimeUnit.DAYS, "yyyy-MM-dd")).build())
                .additionalNeeds("None")
                .build();
    }

    @Test
    void testGetAllBookingIdsWithoutParamsReturns200() {
        BookingRequestDto bookingRequestDto = createBookingRequestDto();
        int id = bookingApi.createBooking(bookingRequestDto).as(BookingResponseDto.class)
                .getBookingId();
        assertBookingIdExists(id, Map.of());
    }

    @Test
    void testGetBookingIdsByOneParamReturns200() {
        BookingRequestDto bookingRequestDto = createBookingRequestDto();
        int id = createBookingAndGetId(bookingRequestDto);

        assertBookingIdExists(id, Map.of(FIRSTNAME, bookingRequestDto.getFirstName()));
        assertBookingIdExists(id, Map.of(LASTNAME, bookingRequestDto.getLastName()));
        assertBookingIdExists(id, Map.of(CHECKIN, bookingRequestDto.getBookingDatesDto().getCheckIn()));
        assertBookingIdExists(id, Map.of(CHECKOUT, bookingRequestDto.getBookingDatesDto().getCheckOut()));
    }

    @Test
    void testGetBookingIdsByTwoParamsReturns200() {
        BookingRequestDto bookingRequestDto = createBookingRequestDto();
        int id = createBookingAndGetId(bookingRequestDto);

        assertBookingIdExists(id, Map.of(FIRSTNAME, bookingRequestDto.getFirstName(),
                LASTNAME, bookingRequestDto.getLastName()));
        assertBookingIdExists(id, Map.of(CHECKIN, bookingRequestDto.getBookingDatesDto().getCheckIn(),
                CHECKOUT, bookingRequestDto.getBookingDatesDto().getCheckOut()));
        assertBookingIdExists(id, Map.of(CHECKOUT, bookingRequestDto.getBookingDatesDto().getCheckOut(),
                FIRSTNAME, bookingRequestDto.getFirstName()));
    }

    @Test
    void testGetByNonValidBookingNameReturns200() {
        Response response = bookingApi.getBookings(Map.of(FIRSTNAME, faker.lordOfTheRings().character()));
        Assertions.assertThat(response.statusCode())
                .as("Response status code should be 200")
                .isEqualTo(SC_OK);
        response = bookingApi.getBookings(Map.of(LASTNAME, faker.lordOfTheRings().character()));
        Assertions.assertThat(response.statusCode())
                .as("Response status code should be 200")
                .isEqualTo(SC_OK);
    }

    @Test
    void testGetByNonValidBookingDataReturns500() {
        Response response = bookingApi.getBookings(Map.of(CHECKIN, "2000-10-0"));
        Assertions.assertThat(response.statusCode()).isEqualTo(SC_INTERNAL_SERVER_ERROR);
        response = bookingApi.getBookings(Map.of(CHECKOUT, "June"));
        Assertions.assertThat(response.statusCode()).isEqualTo(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void testUpdateBookingReturns200() {
        BookingRequestDto bookingRequestDto = createBookingRequestDto();
        int id = createBookingAndGetId(bookingRequestDto);
        Response response = bookingApi.partialUpdateBooking(BookingRequestDto.builder().totalPrice(-9999999).build(), id, token);
        Assertions.assertThat(response.statusCode())
                .as("Response status code should be 200")
                .isEqualTo(SC_OK);
        int newPrice = bookingApi.getBookingById(id).getBody().as(BookingRequestDto.class).getTotalPrice();
        Assertions.assertThat(newPrice).isEqualTo(-9999999);
        Assertions.assertThat(response.statusCode())
                .as("Response status code should be 200")
                .isEqualTo(SC_OK);
    }

    @Test
    void testFullUpdateBookingUpdateAllFields() {
        int id = bookingApi.createBooking(createBookingRequestDto())
                .as(BookingResponseDto.class)
                .getBookingId();

        BookingRequestDto bookingRequestDto = createBookingRequestDto();
        BookingRequestDto updateResponse = bookingApi.partialUpdateBooking(bookingRequestDto, id, token)
                .body().as(BookingRequestDto.class);
        BookingRequestDto getResponse = bookingApi.getBookingById(id).body().as(BookingRequestDto.class);
        Assertions.assertThat(updateResponse).usingRecursiveComparison()
                .as("All fields should be updated")
                .isEqualTo(getResponse);
    }

    @Test
    void testUpdateNonValidIdBookingReturns405() {
        Response updateResponse = bookingApi.partialUpdateBooking(
                BookingRequestDto.builder().totalPrice(9999999).build(), 234234234, token);

        Assertions.assertThat(updateResponse.statusCode()).isEqualTo(SC_METHOD_NOT_ALLOWED);
    }

    @Test
    void testUpdateNonTokenBookingReturns403() {
        Response updateResponse = bookingApi.partialUpdateBooking(
                BookingRequestDto.builder().totalPrice(0).build(), 1, TOKEN);

        Assertions.assertThat(updateResponse.statusCode()).isEqualTo(SC_FORBIDDEN);
    }

    @Test
    void testDeleteNonTokenBookingReturns403() {
        Response response = bookingApi.deleteBooking(
                1, "");

        Assertions.assertThat(response.statusCode()).isEqualTo(SC_FORBIDDEN);
    }

    @Test
    void testDeleteBookingReturns201() {
        BookingRequestDto bookingRequestDto = createBookingRequestDto();
        int id = createBookingAndGetId(bookingRequestDto);
        Response response = bookingApi.deleteBooking(id, token);
        Assertions.assertThat(response.statusCode()).isEqualTo(SC_CREATED);
        response = bookingApi.getBookingById(id);
        Assertions.assertThat(response.statusCode()).isEqualTo(SC_NOT_FOUND);
    }

    @Test
    void testDeleteNonExistingBookingReturns405() {
        Response response = bookingApi.deleteBooking(
                777777777, token);
        Assertions.assertThat(response.statusCode()).isEqualTo(SC_METHOD_NOT_ALLOWED);
    }

    // TODO add xml body tests
    // TODO test BasicAuth
    private int createBookingAndGetId(BookingRequestDto bookingRequestDto) {
        return bookingApi.createBooking(bookingRequestDto)
                .as(BookingResponseDto.class)
                .getBookingId();
    }

    private void assertBookingIdExists(int expectedId, Map<String, String> queryParams) {
        Response response = bookingApi.getBookings(queryParams);

        Assertions.assertThat(response.statusCode())
                .as("Response status code should be 200")
                .isEqualTo(SC_OK);
        Assertions.assertThat(Arrays.stream(response.body().as(BookingResponseDto[].class))
                        .filter(booking -> booking.getBookingId() == expectedId)
                        .count())
                .as("Booking ID should be in the query results")
                .isEqualTo(1);
    }

    private void assertBookingIdExists(int expectedId) {
        assertBookingIdExists(expectedId, Map.of());
    }
}
