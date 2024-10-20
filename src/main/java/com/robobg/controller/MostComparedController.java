package com.robobg.controller;

import com.robobg.entity.dtos.RobotDTO.MostComparedDTO;
import com.robobg.service.MostComparedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/v1/most-compared")
public class MostComparedController {
    private final MostComparedService mostComparedService;

    @Autowired
    public MostComparedController(MostComparedService mostComparedService) {
        this.mostComparedService = mostComparedService;
    }

    @GetMapping
    List<MostComparedDTO> getAllMostCompares(){
        return mostComparedService.getAll();
    }


}
