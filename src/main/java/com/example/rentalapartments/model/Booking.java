package com.example.rentalapartments.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Бронювання. Первинний ключ генерується послідовністю bookings_seq (SEQUENCE).
 * Багато бронювань -> одна квартира.
 */
@Entity
@Table(name = "bookings")
@NamedQuery(name = "Booking.findByStatusNamed",
        query = "SELECT b FROM Booking b WHERE b.status = :status ORDER BY b.dateFrom, b.id")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookings_seq_gen")
    @SequenceGenerator(name = "bookings_seq_gen", sequenceName = "bookings_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    /** Той самий стовпець лише для читання: id квартири доступний без звернення до ледачого proxy. */
    @Column(name = "apartment_id", insertable = false, updatable = false)
    private Long apartmentId;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    public Booking() {}

    public Booking(Apartment apartment, String clientName,
                   LocalDate dateFrom, LocalDate dateTo, String status) {
        setApartment(apartment);
        this.clientName = clientName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Apartment getApartment() { return apartment; }
    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
        this.apartmentId = apartment == null ? null : apartment.getId();
    }
    public Long getApartmentId() { return apartmentId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }
    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
