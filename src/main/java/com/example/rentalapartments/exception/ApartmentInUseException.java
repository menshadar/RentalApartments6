package com.example.rentalapartments.exception;

public class ApartmentInUseException extends RuntimeException {
    public ApartmentInUseException(Long id) {
        super("Квартиру id=" + id + " не можна видалити: для неї існують бронювання");
    }
}
