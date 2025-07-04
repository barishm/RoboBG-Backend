package com.robobg.controller;

import com.robobg.dtos.ConsumableDTO.ConsumableDetailsDTO;
import com.robobg.dtos.ConsumableDTO.ConsumableListDTO;
import com.robobg.service.ConsumableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public Optional<ConsumableDetailsDTO> getRobotById(@PathVariable("id") Long id) {
        return consumableService.getConsumableById(id);
    }



}
