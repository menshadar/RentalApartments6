package com.example.rentalapartments.repository;

import com.example.rentalapartments.model.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Long> {

    // ---------- 5.1.1 JPQL через @Query ----------

    /** Кількість активних бронювань квартири, що перетинаються з вказаним періодом. */
    @Query("""
            SELECT COUNT(b) FROM Booking b
            WHERE b.apartment.id = :apartmentId
              AND b.status = 'ACTIVE'
              AND b.dateFrom < :dateTo
              AND b.dateTo > :dateFrom
            """)
    long countOverlapping(@Param("apartmentId") Long apartmentId,
                          @Param("dateFrom") LocalDate dateFrom,
                          @Param("dateTo") LocalDate dateTo);

    // ---------- 5.1.2 JPQL через @NamedQuery (див. Booking.findByStatusNamed) ----------

    @Query(name = "Booking.findByStatusNamed")
    List<Booking> findByStatus(@Param("status") String status);

    // ---------- 5.2 Методи, згенеровані Spring Data за назвою ----------

    List<Booking> findAllByOrderByIdAsc();

    List<Booking> findByClientNameContainingIgnoreCaseOrderByIdAsc(String clientName);

    List<Booking> findByApartment_IdOrderByIdAsc(Long apartmentId);

    boolean existsByApartment_Id(Long apartmentId);
}
