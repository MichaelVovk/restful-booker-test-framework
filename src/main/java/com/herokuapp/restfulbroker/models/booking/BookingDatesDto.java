package com.herokuapp.restfulbroker.models.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Data
public class BookingDatesDto {
    @JsonProperty("checkin")
    private String checkIn;

    @JsonProperty("checkout")
    private String checkOut;
}
