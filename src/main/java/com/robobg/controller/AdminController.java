package com.robobg.controller;

import com.robobg.entity.dtos.UserIdUsernameRoleDTO;
import com.robobg.entity.dtos.UserUsernameEmailDTO;
import com.robobg.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/v1/admin")
public class AdminController {
    private final UserService userService;
    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }


    @PutMapping("/users")
    public void setRole(@RequestBody UserIdUsernameRoleDTO userIdUsernameRoleDTO) {
        userService.setRole(userIdUsernameRoleDTO);
    }

    @GetMapping("/moderators")
    public List<UserUsernameEmailDTO> getAllModerators() {
        return userService.getAllModerators();
    }


}
