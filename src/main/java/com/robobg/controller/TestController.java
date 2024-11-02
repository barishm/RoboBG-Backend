package com.robobg.controller;


import com.robobg.entity.dtos.RobotDTO.RobotModelImageLinksDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/test")
public class TestController {
    @GetMapping()
    public String returnsHelloWorld(){
        return "Hello world";
    }
}
