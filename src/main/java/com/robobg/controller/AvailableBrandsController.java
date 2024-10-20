package com.robobg.controller;

import com.robobg.entity.AvailableBrands;
import com.robobg.service.impl.AvailableBrandsServiceImpl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/v1/available-brands")
public class AvailableBrandsController {
    private final AvailableBrandsServiceImpl availableBrandsService;
    public AvailableBrandsController(AvailableBrandsServiceImpl availableBrandsService) {
        this.availableBrandsService = availableBrandsService;
    }

    @GetMapping
    List<AvailableBrands> getAllBrands() {
        return availableBrandsService.getAll();
    }
}
