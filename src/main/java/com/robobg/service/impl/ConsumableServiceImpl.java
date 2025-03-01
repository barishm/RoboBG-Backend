package com.robobg.service.impl;

import com.robobg.entity.Consumable;
import com.robobg.entity.Robot;
import com.robobg.entity.dtos.ConsumableDTO.ConsumableListDTO;
import com.robobg.entity.dtos.ConsumableDTO.CreateConsumableDTO;
import com.robobg.repository.ConsumableRepository;
import com.robobg.repository.RobotRepository;
import com.robobg.service.ConsumableService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConsumableServiceImpl implements ConsumableService {

    private final ConsumableRepository consumableRepository;
    private final RobotRepository robotRepository;
    private final ModelMapper modelMapper;
    @Autowired
    public ConsumableServiceImpl(ConsumableRepository consumableRepository, RobotRepository robotRepository, ModelMapper modelMapper) {
        this.consumableRepository = consumableRepository;
        this.robotRepository = robotRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<ConsumableListDTO> getAllConsumables() {
        return consumableRepository.findAll().stream()
                .map(consumable -> modelMapper.map(consumable, ConsumableListDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createConsumableService(CreateConsumableDTO createConsumableDTO) {
        Set<Robot> compatibleRobots = new HashSet<>(robotRepository.findAllById(createConsumableDTO.getCompatibleRobotIds()));

        Consumable consumable = new Consumable();
        consumable.setTitle(createConsumableDTO.getTitle());
        consumable.setDescription(createConsumableDTO.getDescription());
        consumable.setCompatibleRobots(compatibleRobots);

        consumableRepository.save(consumable);
    }

    @Override
    @Transactional
    public void updateConsumable(CreateConsumableDTO updateConsumableDTO) {
        Consumable existingConsumable = consumableRepository.findById(updateConsumableDTO.getId())
                .orElseThrow(() -> new RuntimeException("Consumable not found with ID: " + updateConsumableDTO.getId()));

        if (updateConsumableDTO.getTitle() != null) {
            existingConsumable.setTitle(updateConsumableDTO.getTitle());
        }
        if (updateConsumableDTO.getDescription() != null) {
            existingConsumable.setDescription(updateConsumableDTO.getDescription());
        }

        if (updateConsumableDTO.getCompatibleRobotIds() != null && !updateConsumableDTO.getCompatibleRobotIds().isEmpty()) {
            Set<Robot> updatedRobots = new HashSet<>(robotRepository.findAllById(updateConsumableDTO.getCompatibleRobotIds()));
            existingConsumable.setCompatibleRobots(updatedRobots);
        }

        consumableRepository.save(existingConsumable);
    }

    @Override
    public void deleteConsumable(Long id) {
        consumableRepository.deleteById(id);
    }
}
