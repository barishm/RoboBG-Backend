package com.robobg.repository;

import com.robobg.entity.PurchaseLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseLinkRepository extends JpaRepository<PurchaseLink,Long> {
    List<PurchaseLink> findByRobotId(Long robotId);

}
