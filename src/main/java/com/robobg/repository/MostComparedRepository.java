package com.robobg.repository;

import com.robobg.entity.MostCompared;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MostComparedRepository extends JpaRepository<MostCompared, Long> {

}
