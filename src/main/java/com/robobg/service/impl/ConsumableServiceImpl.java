package com.robobg.service.impl;

import com.robobg.entity.Consumable;
import com.robobg.entity.Robot;
import com.robobg.entity.dtos.ConsumableDTO.ConsumableDetailsDTO;
import com.robobg.entity.dtos.ConsumableDTO.ConsumableListDTO;
import com.robobg.entity.dtos.ConsumableDTO.CreateConsumableDTO;
import com.robobg.exceptions.EntityNotFoundException;
import com.robobg.repository.ConsumableRepository;
import com.robobg.repository.RobotRepository;
import com.robobg.service.ConsumableService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConsumableServiceImpl implements ConsumableService {

    private static final String SAVE_IMAGE_PATH = "/home/ubuntu/robobg/images/consumables";
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
        Set<Robot> compatibleRobots = new HashSet<>(robotRepository.findAllById(createConsumableDTO.getRobotIds()));

        Consumable consumable = new Consumable();
        consumable.setTitle(createConsumableDTO.getTitle());
        consumable.setDescription(createConsumableDTO.getDescription());
        if (createConsumableDTO.getPrice() != null) {
            consumable.setPrice(new BigDecimal(createConsumableDTO.getPrice()));
        }
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

        if (updateConsumableDTO.getPrice() != null && !updateConsumableDTO.getPrice().trim().isEmpty()) {
            try {
                existingConsumable.setPrice(new BigDecimal(updateConsumableDTO.getPrice()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid price format: " + updateConsumableDTO.getPrice());
            }
        }

        if (updateConsumableDTO.getRobotIds() != null && !updateConsumableDTO.getRobotIds().isEmpty()) {
            Set<Robot> updatedRobots = new HashSet<>(robotRepository.findAllById(updateConsumableDTO.getRobotIds()));
            existingConsumable.setCompatibleRobots(updatedRobots);
        }

        consumableRepository.save(existingConsumable);
    }

    @Override
    public void deleteConsumable(Long id) throws EntityNotFoundException {
        Optional<Consumable> consumableOptional = consumableRepository.findById(id);
        if (consumableOptional.isPresent()) {
            Consumable consumable = consumableOptional.get();

            for (String imageName : consumable.getImages()) {
                Path imagePath = Paths.get(SAVE_IMAGE_PATH, imageName);
                try {
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    System.err.println("Failed to delete image: " + imagePath + " - " + e.getMessage());
                }
            }

            consumableRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Consumable with ID " + id + " not found.");
        }
    }

    @Override
    public Optional<ConsumableDetailsDTO> getConsumableById(Long id) {
        return consumableRepository.findById(id)
                .map(consumable -> modelMapper.map(consumable, ConsumableDetailsDTO.class));
    }


    @Override
    public void uploadConsumableImage(Long consumableId, List<MultipartFile> files) throws IOException {
        Optional<Consumable> consumableOptional = consumableRepository.findById(consumableId);
        if (consumableOptional.isEmpty()) {
            throw new IllegalArgumentException("Consumable with ID " + consumableId + " does not exist.");
        }

        Consumable consumable = consumableOptional.get();

        for (String imageName : consumable.getImages()) {
            Path oldImagePath = Paths.get(SAVE_IMAGE_PATH, imageName);
            Files.deleteIfExists(oldImagePath);
        }

        consumable.getImages().clear();
        consumableRepository.save(consumable);

        List<String> newImageNames = new ArrayList<>();

        for (MultipartFile file : files) {
            String extension = FileUtils.getExtensionOfFile(file);
            if (extension.isEmpty()) {
                throw new IllegalArgumentException("Unsupported file type.");
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String uniqueId = UUID.randomUUID().toString();
            String fileName = String.format("Consumable%s_%s_%s.%s", consumableId, timestamp, uniqueId, extension);

            Path imagePath = Paths.get(SAVE_IMAGE_PATH, fileName);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, file.getBytes());

            newImageNames.add(fileName);
        }

        consumable.setImages(newImageNames);
        consumableRepository.save(consumable);
    }
}
