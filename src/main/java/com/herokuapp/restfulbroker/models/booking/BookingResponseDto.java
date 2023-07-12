package com.herokuapp.restfulbroker.models.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingResponseDto {
    @JsonProperty("bookingid")
    private int bookingId;

    @JsonProperty("booking")
    private BookingRequestDto bookingRequestDto;
}
