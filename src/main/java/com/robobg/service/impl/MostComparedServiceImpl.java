package com.robobg.service.impl;

import com.robobg.entity.MostCompared;
import com.robobg.entity.dtos.RobotDTO.CreateMostComparedDTO;
import com.robobg.entity.dtos.RobotDTO.MostComparedDTO;
import com.robobg.entity.dtos.RobotDTO.RobotModelDTO;
import com.robobg.entity.dtos.RobotDTO.UpdateMostComparedDTO;
import com.robobg.repository.MostComparedRepository;
import com.robobg.repository.RobotRepository;
import com.robobg.service.MostComparedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MostComparedServiceImpl implements MostComparedService {
    private final MostComparedRepository mostComparedRepository;
    private final RobotRepository robotRepository;

    @Autowired
    public MostComparedServiceImpl(MostComparedRepository mostComparedRepository, RobotRepository robotRepository) {
        this.mostComparedRepository = mostComparedRepository;
        this.robotRepository = robotRepository;
    }

    public List<MostComparedDTO> getAll() {
        List<MostComparedDTO> mostComparedDTOList = new ArrayList<>();
        List<MostCompared> mostComparedList = mostComparedRepository.findAll();

        for (MostCompared mostCompared : mostComparedList) {
            MostComparedDTO mostComparedDTO = new MostComparedDTO();
            mostComparedDTO.setId(mostCompared.getId());
            mostComparedDTO.setOrder(mostCompared.getOrder());

            // Process robot1
            Long robot1 = mostCompared.getRobot1();
            if (robot1 != null) {
                String model = robotRepository.findModelById(robot1);
                if (model != null) {
                    RobotModelDTO robotModelDTO1 = new RobotModelDTO();
                    robotModelDTO1.setModel(model);
                    robotModelDTO1.setId(robot1);
                    mostComparedDTO.setRobot1(robotModelDTO1);
                }
            }

            // Process robot2
            Long robot2 = mostCompared.getRobot2();
            if (robot2 != null) {
                String model = robotRepository.findModelById(robot2);
                if (model != null) {
                    RobotModelDTO robotModelDTO2 = new RobotModelDTO();
                    robotModelDTO2.setModel(model);
                    robotModelDTO2.setId(robot2);
                    mostComparedDTO.setRobot2(robotModelDTO2);
                }
            }

            // Process robot3
            Long robot3 = mostCompared.getRobot3();
            if (robot3 != null) {
                String model = robotRepository.findModelById(robot3); // Corrected to use robot3
                if (model != null) {
                    RobotModelDTO robotModelDTO3 = new RobotModelDTO();
                    robotModelDTO3.setModel(model);
                    robotModelDTO3.setId(robot3);
                    mostComparedDTO.setRobot3(robotModelDTO3);
                }
            }

            mostComparedDTOList.add(mostComparedDTO);
        }
        mostComparedDTOList.sort(Comparator.comparing(MostComparedDTO::getOrder));
        return mostComparedDTOList;
    }

    @Override
    public void createMostCompared(CreateMostComparedDTO createMostComparedDTO) {
        MostCompared mostCompared = new MostCompared();
        mostCompared.setOrder(createMostComparedDTO.getOrder());
        Long robot1 = createMostComparedDTO.getRobot1();
        if(robot1 != null) {
            boolean isExist = robotRepository.existsById(robot1);
            if(isExist) {
                mostCompared.setRobot1(robot1);
            }
        }
        Long robot2 = createMostComparedDTO.getRobot2();
        if(robot2 != null) {
            boolean isExist = robotRepository.existsById(robot2);
            if(isExist) {
                mostCompared.setRobot2(robot2);
            }
        }
        Long robot3 = createMostComparedDTO.getRobot3();
        if(robot3 != null) {
            boolean isExist = robotRepository.existsById(robot3);
            if(isExist) {
                mostCompared.setRobot3(robot3);
            }
        }
        mostComparedRepository.save(mostCompared);
    }

    @Override
    public void updateMostCompared(UpdateMostComparedDTO updateMostComparedDTO) {
        MostCompared mostCompared = new MostCompared();
        mostCompared.setId(updateMostComparedDTO.getId());
        Long robot1 = updateMostComparedDTO.getRobot1();
        if(robot1 != null) {
            boolean isExist = robotRepository.existsById(robot1);
            if(isExist) {
                mostCompared.setRobot1(robot1);
            }
        }
        Long robot2 = updateMostComparedDTO.getRobot2();
        if(robot2 != null) {
            boolean isExist = robotRepository.existsById(robot2);
            if(isExist) {
                mostCompared.setRobot2(robot2);
            }
        }
        Long robot3 = updateMostComparedDTO.getRobot3();
        if(robot3 != null) {
            boolean isExist = robotRepository.existsById(robot3);
            if(isExist) {
                mostCompared.setRobot3(robot3);
            }
        }
        mostComparedRepository.save(mostCompared);
    }

    @Override
    public void deleteMostCompared(Long id) {
        mostComparedRepository.deleteById(id);
    }

    @Override
    public void deleteMostComparedEntityIfRobotWithIdExist(Long id) {
        List<MostCompared> mostComparedList = mostComparedRepository.findAll();
        for (MostCompared mostCompared : mostComparedList) {
            if(mostCompared.getRobot1() == id || mostCompared.getRobot2() == id || mostCompared.getRobot3() == id){
                deleteMostCompared(mostCompared.getId());
            }
        }
    }
}
