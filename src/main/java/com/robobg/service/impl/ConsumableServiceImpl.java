package com.robobg.service.impl;

import com.robobg.entity.Consumable;
import com.robobg.entity.Robot;
import com.robobg.entity.dtos.ConsumableDTO.CreateConsumableDTO;
import com.robobg.repository.ConsumableRepository;
import com.robobg.repository.RobotRepository;
import com.robobg.service.ConsumableService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
@Service
public class ConsumableServiceImpl implements ConsumableService {

    private final ConsumableRepository consumableRepository;
    private final RobotRepository robotRepository;

    @Autowired
    public ConsumableServiceImpl(ConsumableRepository consumableRepository, RobotRepository robotRepository) {
        this.consumableRepository = consumableRepository;
        this.robotRepository = robotRepository;
    }


    @Override
    @Transactional
    public void createConsumableService(CreateConsumableDTO createConsumableDTO) {
        Set<Robot> compatibleRobots = new HashSet<>(robotRepository.findAllById(createConsumableDTO.getCompatibleRobotIds()));

        Consumable consumable = new Consumable();
        consumable.setTitle(createConsumableDTO.getTitle());
        consumable.setDescription(createConsumableDTO.getDescription());
        consumable.setImages(createConsumableDTO.getImages());
        consumable.setCompatibleRobots(compatibleRobots);

        consumableRepository.save(consumable);
    }

    @Override
    @Transactional
    public void updateConsumable(Long consumableId, CreateConsumableDTO updateConsumableDTO) {
        Consumable existingConsumable = consumableRepository.findById(consumableId)
                .orElseThrow(() -> new RuntimeException("Consumable not found with ID: " + consumableId));

        if (updateConsumableDTO.getTitle() != null) {
            existingConsumable.setTitle(updateConsumableDTO.getTitle());
        }
        if (updateConsumableDTO.getDescription() != null) {
            existingConsumable.setDescription(updateConsumableDTO.getDescription());
        }
        if (updateConsumableDTO.getImages() != null && !updateConsumableDTO.getImages().isEmpty()) {
            existingConsumable.setImages(updateConsumableDTO.getImages());
        }

        if (updateConsumableDTO.getCompatibleRobotIds() != null && !updateConsumableDTO.getCompatibleRobotIds().isEmpty()) {
            Set<Robot> updatedRobots = new HashSet<>(robotRepository.findAllById(updateConsumableDTO.getCompatibleRobotIds()));
            existingConsumable.setCompatibleRobots(updatedRobots);
        }

        consumableRepository.save(existingConsumable);
    }
}
