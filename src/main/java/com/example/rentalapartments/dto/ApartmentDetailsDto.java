package com.example.rentalapartments.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Квартира разом з усіма її бронюваннями")
public class ApartmentDetailsDto {

    @Schema(description = "Дані квартири")
    private ApartmentDto apartment;

    @Schema(description = "Бронювання цієї квартири")
    private List<BookingDto> bookings;

    public ApartmentDetailsDto(ApartmentDto apartment, List<BookingDto> bookings) {
        this.apartment = apartment;
        this.bookings = bookings;
    }

    public ApartmentDto getApartment() { return apartment; }
    public List<BookingDto> getBookings() { return bookings; }
}
