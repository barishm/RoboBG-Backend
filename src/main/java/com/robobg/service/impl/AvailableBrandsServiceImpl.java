package com.robobg.service.impl;

import com.robobg.entity.AvailableBrands;
import com.robobg.repository.AvailableBrandsRepository;
import com.robobg.service.AvailableBrandsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AvailableBrandsServiceImpl implements AvailableBrandsService {
    private final AvailableBrandsRepository availableBrandsRepository;

    @Autowired
    public AvailableBrandsServiceImpl(AvailableBrandsRepository availableBrandsRepository) {
        this.availableBrandsRepository = availableBrandsRepository;
    }


    @Override
    public void increaseCount(String brand) {
        Optional<AvailableBrands> byBrand = availableBrandsRepository.findByBrand(brand);
        AvailableBrands availableBrands;
        if(byBrand.isPresent()){
            availableBrands = byBrand.get();
            availableBrands.setCount(availableBrands.getCount() + 1);
        } else {
            availableBrands = new AvailableBrands();
            availableBrands.setBrand(brand);
            availableBrands.setCount(1);
        }
        availableBrandsRepository.save(availableBrands);
    }

    @Override
    public void decreaseCount(String brand) {
        Optional<AvailableBrands> byBrand = availableBrandsRepository.findByBrand(brand);
        if(byBrand.isPresent()){
            AvailableBrands availableBrands = byBrand.get();
            if(availableBrands.getCount() <= 1){
                availableBrandsRepository.delete(availableBrands);
            } else  {
                availableBrands.setCount(availableBrands.getCount() - 1);
                availableBrandsRepository.save(availableBrands);
            }
        }
    }

    @Override
    public List<AvailableBrands> getAll() {
        List<AvailableBrands> brands = availableBrandsRepository.findAll();
        brands.sort(Comparator.comparing(AvailableBrands::getCount, Collections.reverseOrder()));
        return brands;
    }

}
