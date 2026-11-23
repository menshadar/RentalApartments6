package com.example.rentalapartments.mapper;

import com.example.rentalapartments.dto.BookingDto;
import com.example.rentalapartments.model.Booking;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setApartmentId(booking.getApartmentId());
        dto.setClientName(booking.getClientName());
        dto.setDateFrom(booking.getDateFrom());
        dto.setDateTo(booking.getDateTo());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    /** Повертає бронювання без квартири: зв'язок із Apartment встановлює сервіс. */
    public static Booking toEntity(BookingDto dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setClientName(dto.getClientName());
        booking.setDateFrom(dto.getDateFrom());
        booking.setDateTo(dto.getDateTo());
        return booking;
    }
}
