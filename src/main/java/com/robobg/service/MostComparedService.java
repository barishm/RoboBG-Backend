package com.robobg.service;

import com.robobg.entity.dtos.RobotDTO.CreateMostComparedDTO;
import com.robobg.entity.dtos.RobotDTO.MostComparedDTO;
import com.robobg.entity.dtos.RobotDTO.UpdateMostComparedDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MostComparedService {
    List<MostComparedDTO> getAll();

    void createMostCompared(CreateMostComparedDTO createMostComparedDTO);

    void updateMostCompared(UpdateMostComparedDTO updateMostComparedDTO);

    void deleteMostCompared(Long id);

    void deleteMostComparedEntityIfRobotWithIdExist(Long id);
}
