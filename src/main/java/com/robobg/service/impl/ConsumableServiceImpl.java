package com.robobg.service.impl;

import com.robobg.entity.Consumable;
import com.robobg.entity.Robot;
import com.robobg.dtos.ConsumableDTO.ConsumableDetailsDTO;
import com.robobg.dtos.ConsumableDTO.ConsumableListDTO;
import com.robobg.dtos.ConsumableDTO.CreateConsumableDTO;
import com.robobg.exceptions.EntityNotFoundException;
import com.robobg.repository.ConsumableRepository;
import com.robobg.repository.RobotRepository;
import com.robobg.service.ConsumableService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConsumableServiceImpl implements ConsumableService {

    @Value("${app.files.path}")
    private String appFilesPath;
    @Value("${domain}")
    private String domain;
    @Value("${web.protocol}")
    private String webProtocol;
    private final ConsumableRepository consumableRepository;
    private final RobotRepository robotRepository;
    private final ModelMapper modelMapper;
    private  final ImageService imageService;

    @Autowired
    public ConsumableServiceImpl(ConsumableRepository consumableRepository,
                                 RobotRepository robotRepository,
                                 ModelMapper modelMapper,
                                 ImageService imageService) {
        this.consumableRepository = consumableRepository;
        this.robotRepository = robotRepository;
        this.modelMapper = modelMapper;
        this.imageService = imageService;
    }

    private String buildFullImageUrl(String image) {
        if (image == null || image.startsWith("http")) {
            return image;
        }
        return webProtocol+"://api."+domain+"/files" + "/" + image;
    }


    @Override
    public List<ConsumableListDTO> getAllConsumables() {
        return consumableRepository.findAll().stream()
                .map(consumable -> {
                    ConsumableListDTO dto = modelMapper.map(consumable, ConsumableListDTO.class);

                    List<String> updatedImages = dto.getImages().stream()
                            .map(this::buildFullImageUrl)
                            .toList();
                    dto.setImages(updatedImages);

                    return dto;
                })
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
                Path imagePath = Paths.get(appFilesPath, imageName);
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
                .map(consumable -> {
                    ConsumableDetailsDTO dto = modelMapper.map(consumable, ConsumableDetailsDTO.class);

                    List<String> updatedImages = dto.getImages().stream()
                            .map(this::buildFullImageUrl)
                            .toList();
                    dto.setImages(updatedImages);

                    return dto;
                });
    }


    @Override
    public void uploadConsumableImage(Long consumableId, List<MultipartFile> files) throws IOException {

        Consumable consumable = consumableRepository.findById(consumableId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Consumable with ID " + consumableId + " does not exist."
                ));

        // 1. Delete old images FIRST (after we know entity exists)
        if (consumable.getImages() != null) {
            for (String imageName : consumable.getImages()) {
                Path oldImagePath = Paths.get("app/files", imageName);
                Files.deleteIfExists(oldImagePath);
            }
        }

        // 2. Save new images via ImageService
        List<String> newImages = imageService.storeConsumablesImages(consumableId, files);

        // 3. Update DB
        consumable.setImages(newImages);
        consumableRepository.save(consumable);
    }
}
