package com.example.school.repository;

import com.example.school.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByCountryCodeOrderByNameAsc(String countryCode);
    Optional<City> findByNameAndCountryCode(String name, String countryCode);
}
