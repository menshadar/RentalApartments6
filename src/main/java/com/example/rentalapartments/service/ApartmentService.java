package com.example.rentalapartments.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.rentalapartments.model.Apartment;
import com.example.rentalapartments.model.SearchCriteria;

public interface ApartmentService {

	// ---- сценарій клієнта (сторінка пошуку) ----
	List<Apartment> search(SearchCriteria c);

	// ---- REST: фільтрація + пагінація на рівні БД ----
	Page<Apartment> search(SearchCriteria c, int page, int size);

	// ---- сценарії хазяїна / CRUD ----
	List<Apartment> findAll();

	Apartment findById(Long id);

	Apartment save(Apartment apartment);

	void delete(Long id);

	// ---- ЛР6: запити Spring Data ----
	List<Apartment> findCheaperThan(int maxPrice);

	List<Apartment> findCheapest();

	List<Apartment> findByCity(String city);

	Apartment findByIdWithBookings(Long id);

}
