package com.robobg.service;

import com.robobg.entity.dtos.RobotDTO.CreateMostComparedDTO;
import com.robobg.entity.dtos.RobotDTO.MostComparedDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MostComparedService {
    List<MostComparedDTO> getAll();

    void createMostCompared(CreateMostComparedDTO createMostComparedDTO);

    void updateMostCompared(CreateMostComparedDTO updateMostComparedDTO);

    void deleteMostCompared(Long id);

    void deleteMostComparedEntityIfRobotWithIdExist(Long id);
}
