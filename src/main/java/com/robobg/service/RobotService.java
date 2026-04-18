package com.robobg.service;

import com.robobg.dtos.RobotDTO.CreateRobotDTO;
import com.robobg.dtos.RobotDTO.RobotDTO;
import com.robobg.dtos.RobotDTO.RobotsListDTO;
import com.robobg.exceptions.RobotAlreadyExistsException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public interface RobotService {


    void saveRobot(CreateRobotDTO robot) throws RobotAlreadyExistsException;
    void updateRobot(CreateRobotDTO robot);
    void deleteRobotById(Long id) throws ChangeSetPersister.NotFoundException;
    Optional<RobotDTO> getRobotById(Long id);
    void uploadRobotImage(Long robotId, MultipartFile file) throws IOException;
    List<RobotsListDTO> getAllRobots();
}
