package com.example.rentalapartments.repository;

import com.example.rentalapartments.model.Apartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApartmentRepository extends CrudRepository<Apartment, Long> {

    // ---------- 5.1.1 JPQL через @Query ----------

    /**
     * Пошук за критеріями з пагінацією. Параметри не можуть бути null:
     * "" (місто), 0 (кімнати) означають «будь-яке», maxPrice/minArea мають значення за замовчуванням.
     */
    @Query("""
            SELECT a FROM Apartment a
            WHERE (:city = '' OR LOWER(a.city) = LOWER(:city))
              AND (:rooms = 0 OR a.rooms = :rooms)
              AND a.price <= :maxPrice
              AND a.area >= :minArea
            """)
    Page<Apartment> search(@Param("city") String city,
                           @Param("rooms") int rooms,
                           @Param("maxPrice") int maxPrice,
                           @Param("minArea") double minArea,
                           Pageable pageable);

    /** JOIN FETCH: квартира разом із бронюваннями одним запитом (уникаємо проблеми N+1). */
    @Query("SELECT DISTINCT a FROM Apartment a LEFT JOIN FETCH a.bookings WHERE a.id = :id")
    Optional<Apartment> findByIdWithBookings(@Param("id") Long id);

    // ---------- 5.1.2 JPQL через @NamedQuery (див. Apartment.findCheaperThanNamed) ----------

    @Query(name = "Apartment.findCheaperThanNamed")
    List<Apartment> findCheaperThan(@Param("maxPrice") int maxPrice);

    // ---------- 5.2 Методи, згенеровані Spring Data за назвою ----------

    List<Apartment> findAllByOrderByIdAsc();

    List<Apartment> findByCityIgnoreCaseOrderByPriceAsc(String city);

    /** Обмеження кількості результатів ключовим словом Top3. */
    List<Apartment> findTop3ByOrderByPriceAsc();
}
