package com.example.rentalapartments.service;

import com.example.rentalapartments.exception.ApartmentNotFoundException;
import com.example.rentalapartments.exception.BookingConflictException;
import com.example.rentalapartments.exception.BookingNotFoundException;
import com.example.rentalapartments.model.Apartment;
import com.example.rentalapartments.model.Booking;
import com.example.rentalapartments.repository.ApartmentRepository;
import com.example.rentalapartments.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ApartmentRepository apartmentRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                          ApartmentRepository apartmentRepository) {
        this.bookingRepository = bookingRepository;
        this.apartmentRepository = apartmentRepository;
    }

    @Override
	public List<Booking> findAll() {
        return bookingRepository.findAllByOrderByIdAsc();
    }

    @Override
	public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    @Override
	public List<Booking> findByClientName(String clientName) {
        return bookingRepository.findByClientNameContainingIgnoreCaseOrderByIdAsc(clientName.trim());
    }

    @Override
	public List<Booking> findByApartmentId(Long apartmentId) {
        return bookingRepository.findByApartment_IdOrderByIdAsc(apartmentId);
    }

    @Override
	@Transactional
    public Booking create(Long apartmentId, String clientName, LocalDate dateFrom, LocalDate dateTo) {
        return book(apartmentId, clientName, dateFrom, dateTo);
    }

    /**
     * Бронює кілька квартир в одній транзакції: або всі, або жодної.
     * Якщо для будь-якої квартири виникає помилка, уже створені
     * бронювання цього виклику скасовуються (rollback).
     */
    @Override
	@Transactional
    public List<Booking> createMany(String clientName, List<Long> apartmentIds,
                                    LocalDate dateFrom, LocalDate dateTo) {
        List<Booking> result = new ArrayList<>();
        for (Long apartmentId : apartmentIds) {
            result.add(book(apartmentId, clientName, dateFrom, dateTo));
        }
        return result;
    }

    @Override
	public List<Booking> findByStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
	@Transactional
    public Booking cancel(Long id) {
        Booking booking = findById(id);
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    @Override
	@Transactional
    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException(id);
        }
        bookingRepository.deleteById(id);
    }

    private Booking book(Long apartmentId, String clientName,
                         LocalDate dateFrom, LocalDate dateTo) {
        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException("Ім’я клієнта не може бути порожнім");
        }
        if (dateFrom == null || dateTo == null || !dateTo.isAfter(dateFrom)) {
            throw new IllegalArgumentException("Дата завершення має бути пізнішою за дату початку");
        }
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
        if (bookingRepository.countOverlapping(apartmentId, dateFrom, dateTo) > 0) {
            throw new BookingConflictException(apartmentId, dateFrom, dateTo);
        }
        return bookingRepository.save(
                new Booking(apartment, clientName.trim(), dateFrom, dateTo, "ACTIVE"));
    }
}
