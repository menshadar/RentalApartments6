package com.example.rentalapartments.service;

import com.example.rentalapartments.exception.ApartmentInUseException;
import com.example.rentalapartments.exception.ApartmentNotFoundException;
import com.example.rentalapartments.model.Apartment;
import com.example.rentalapartments.model.SearchCriteria;
import com.example.rentalapartments.model.SearchLog;
import com.example.rentalapartments.repository.ApartmentRepository;
import com.example.rentalapartments.repository.BookingRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ApartmentServiceImpl implements ApartmentService {

    private static final Sort BY_ID = Sort.by("id");

    private final ApartmentRepository repository;
    private final BookingRepository bookingRepository;
    private final ObjectProvider<SearchLog> searchLogs;
    private ApartmentValidator validator;

    public ApartmentServiceImpl(ApartmentRepository repository,
                                BookingRepository bookingRepository,
                                ObjectProvider<SearchLog> searchLogs) {
        this.repository = repository;
        this.bookingRepository = bookingRepository;
        this.searchLogs = searchLogs;
    }

    @Autowired
    public void setValidator(ApartmentValidator validator) {
        this.validator = validator;
    }

    // ---- сценарій клієнта (сторінка пошуку) ----
    @Override
    public List<Apartment> search(SearchCriteria c) {
        List<Apartment> result = find(c, Pageable.unpaged(BY_ID)).getContent();
        SearchLog log = searchLogs.getObject();
        log.print(c, result.size());
        return result;
    }

    // ---- REST: фільтрація + пагінація (LIMIT/OFFSET виконує СУБД) ----
    @Override
    public Page<Apartment> search(SearchCriteria c, int page, int size) {
        return find(c, PageRequest.of(page, size, BY_ID));
    }

    private Page<Apartment> find(SearchCriteria c, Pageable pageable) {
        String city = c.getCity() == null ? "" : c.getCity().trim();
        int rooms = c.getRooms() == null ? 0 : c.getRooms();
        int maxPrice = c.getMaxPrice() == null ? Integer.MAX_VALUE : c.getMaxPrice();
        double minArea = c.getMinArea() == null ? 0.0 : c.getMinArea();
        return repository.search(city, rooms, maxPrice, minArea, pageable);
    }

    // ---- сценарії хазяїна / CRUD ----
    @Override
    public List<Apartment> findAll() { return repository.findAllByOrderByIdAsc(); }

    @Override
    public Apartment findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    @Override
    @Transactional
    public Apartment save(Apartment apartment) {
        validator.validate(apartment);
        if (apartment.getId() == null) {
            return repository.save(apartment);
        }
        // оновлення: змінюємо поля керованої сутності, колекцію бронювань не чіпаємо
        Apartment existing = findById(apartment.getId());
        existing.setTitle(apartment.getTitle());
        existing.setDescription(apartment.getDescription());
        existing.setCity(apartment.getCity());
        existing.setRooms(apartment.getRooms());
        existing.setArea(apartment.getArea());
        existing.setPrice(apartment.getPrice());
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ApartmentNotFoundException(id);
        }
        if (bookingRepository.existsByApartment_Id(id)) {
            throw new ApartmentInUseException(id);
        }
        repository.deleteById(id);
    }

    // ---- ЛР6: запити Spring Data ----
    @Override
    public List<Apartment> findCheaperThan(int maxPrice) {
        return repository.findCheaperThan(maxPrice);
    }

    @Override
    public List<Apartment> findCheapest() {
        return repository.findTop3ByOrderByPriceAsc();
    }

    @Override
    public List<Apartment> findByCity(String city) {
        return repository.findByCityIgnoreCaseOrderByPriceAsc(city.trim());
    }

    @Override
    public Apartment findByIdWithBookings(Long id) {
        return repository.findByIdWithBookings(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }
}
