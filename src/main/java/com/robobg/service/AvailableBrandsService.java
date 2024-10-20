package com.robobg.service;

import com.robobg.entity.AvailableBrands;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AvailableBrandsService {

    void increaseCount(String brand);

    void decreaseCount(String brand);

    List<AvailableBrands> getAll();
}
