package com.robobg.controller;

import com.robobg.service.ConsumableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/consumable")
public class ConsumableController {

    private final ConsumableService consumableService;

@Autowired
    public ConsumableController(ConsumableService consumableService) {
        this.consumableService = consumableService;
    }


}
