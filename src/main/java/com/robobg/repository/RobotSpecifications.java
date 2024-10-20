package com.robobg.repository;

import com.robobg.entity.Robot;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RobotSpecifications {
    public static Specification<Robot> hasSuctionPowerBetween(Integer minSuctionPower, Integer maxSuctionPower) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("cleaningFeatures").get("suctionPower"), minSuctionPower, maxSuctionPower);
    }
    public static Specification<Robot> hasDustbinCapacityBetween(Integer minDustbinCapacity, Integer maxDustbinCapacity) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("cleaningFeatures").get("dustbinCapacity"), minDustbinCapacity, maxDustbinCapacity);
    }

    public static Specification<Robot> hasReleaseYearBetween(Integer minReleaseYear, Integer maxReleaseYear) {
        return (root, query, criteriaBuilder) -> {
            Expression<Integer> releaseYear = criteriaBuilder.function("year", Integer.class, root.get("otherSpecifications").get("releaseDate"));
            return criteriaBuilder.between(releaseYear, minReleaseYear, maxReleaseYear);
        };
    }
    public static Specification<Robot> modelContains(String model) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("model"), "%" + model + "%");
    }

    public static Specification<Robot> brandIn(List<String> brands) {
        return (root, query, criteriaBuilder) -> root.get("brand").in(brands);
    }
}
