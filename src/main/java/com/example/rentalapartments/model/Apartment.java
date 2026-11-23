package com.example.rentalapartments.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Квартира. Первинний ключ генерується autoincrement-полем БД (IDENTITY).
 * Містить іменований JPQL-запит (@NamedQuery), який використовує ApartmentRepository.
 */
@Entity
@Table(name = "apartments")
@NamedQuery(name = "Apartment.findCheaperThanNamed",
        query = "SELECT a FROM Apartment a WHERE a.price <= :maxPrice ORDER BY a.price, a.id")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false)
    private int rooms;

    @Column(nullable = false)
    private double area;

    @Column(nullable = false)
    private int price;

    /** Одна квартира -> багато бронювань. Зворотний (inverse) бік зв'язку, завантажується ліниво. */
    @OneToMany(mappedBy = "apartment")
    private List<Booking> bookings = new ArrayList<>();

    public Apartment() {}

    public Apartment(Long id, String title, String description,
                     String city, int rooms, double area, int price) {
        this.id = id; this.title = title; this.description = description;
        this.city = city; this.rooms = rooms; this.area = area; this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public int getRooms() { return rooms; }
    public void setRooms(int rooms) { this.rooms = rooms; }
    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
}
