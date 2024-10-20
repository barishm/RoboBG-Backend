package com.robobg.repository;

import com.robobg.entity.Robot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RobotRepository extends JpaRepository<Robot,Long>, JpaSpecificationExecutor<Robot> {
    @Query("FROM Robot r WHERE r.bests IS NOT NULL")
    List<Robot> findAllBests();
    boolean existsByModel(String model);
    @Query("SELECT r.image FROM Robot r WHERE r.id = :id")
    String findImageById(@Param("id") Long id);

    @Query("SELECT r.model FROM Robot r WHERE r.id = :id")
    String findModelById(Long id);

}
