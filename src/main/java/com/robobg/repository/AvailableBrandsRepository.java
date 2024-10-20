package com.robobg.repository;

import com.robobg.entity.AvailableBrands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailableBrandsRepository extends JpaRepository<AvailableBrands,Long> {
    boolean existsByBrand(String brand);
    Optional<AvailableBrands> findByBrand(String brand);
}
