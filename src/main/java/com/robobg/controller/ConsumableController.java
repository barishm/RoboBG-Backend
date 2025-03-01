package com.robobg.controller;

import com.robobg.entity.dtos.ConsumableDTO.ConsumableListDTO;
import com.robobg.entity.dtos.ConsumableDTO.CreateConsumableDTO;
import com.robobg.service.ConsumableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/consumable")
public class ConsumableController {

    private final ConsumableService consumableService;

    @Autowired
    public ConsumableController(ConsumableService consumableService) {
        this.consumableService = consumableService;
    }


//    @DeleteMapping("/{id}")
//    public void deleteConsumable(@PathVariable Long id) {
//        consumableService.deleteConsumable(id);
//    }
//
//    @PostMapping
//    public void createConsumable(@RequestBody CreateConsumableDTO createConsumableDTO){
//        consumableService.createConsumableService(createConsumableDTO);
//    }
//
//    @PutMapping
//    public void updateConsumable(@RequestBody CreateConsumableDTO updateConsumableDTO) {
//        consumableService.updateConsumable(updateConsumableDTO);
//    }

    @GetMapping
    public List<ConsumableListDTO> getAllConsumables() {
        return consumableService.getAllConsumables();
    }



}
