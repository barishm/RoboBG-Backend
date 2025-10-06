package com.robobg.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.robobg.dtos.RobotDTO.CreateRobotDTO;
import com.robobg.entity.Role;
import com.robobg.entity.User;
import com.robobg.repository.RobotRepository;
import com.robobg.repository.UserRepository;
import com.robobg.service.impl.RobotServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final RobotRepository robotRepository;
    private final RobotServiceImpl robotService;
    @Value("${ADMIN_USERNAME}")
    private String adminUsername;
    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;
    @Value("${ADMIN_EMAIL}")
    private String adminEmail;


    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, ObjectMapper objectMapper, RobotRepository robotRepository, RobotServiceImpl robotService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.robotRepository = robotRepository;
        this.robotService = robotService;
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<User> existingAdminOpt = userRepository.findByUsername(adminUsername);

        if (existingAdminOpt.isEmpty()) {
            // Admin does not exist, create a new one
            User user = new User();
            user.setUsername(adminUsername);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole(Role.ADMIN);
            user.setEmail(adminEmail);
            userRepository.save(user);
            System.out.println("CREATED ADMIN");
        } else {
            User existingAdmin = existingAdminOpt.get();

            // Check if email is null or empty, and update if needed
            if (existingAdmin.getEmail() == null || existingAdmin.getEmail().isBlank()) {
                existingAdmin.setEmail(adminEmail);
                userRepository.save(existingAdmin);
                System.out.println("UPDATED ADMIN EMAIL");
            } else {
                System.out.println("THERE IS ALREADY AN ADMIN WITH EMAIL");
            }
        }

//        InputStream inputStream = getClass().getResourceAsStream("/robots.json");
//        CreateRobotDTO[] data = objectMapper.readValue(inputStream, CreateRobotDTO[].class);
//
//        List<CreateRobotDTO> robotsToSave = Arrays.stream(data)
//                .filter(robot -> !robotRepository.existsByModel(robot.getModel()))
//                .toList();
//
//        for (CreateRobotDTO robot : robotsToSave) {
//            robotService.saveRobot(robot);
//        }
    }
}
