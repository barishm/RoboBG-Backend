package com.robobg.service.impl;

import com.robobg.entity.Consumable;
import com.robobg.entity.Robot;
import com.robobg.entity.dtos.ConsumableDTO.ConsumableDetailsDTO;
import com.robobg.entity.dtos.ConsumableDTO.ConsumableListDTO;
import com.robobg.entity.dtos.ConsumableDTO.CreateConsumableDTO;
import com.robobg.entity.dtos.RobotDTO.RobotDTO;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConsumableServiceImpl implements ConsumableService {

    private final ConsumableRepository consumableRepository;
    private final RobotRepository robotRepository;
    private final S3Service s3Service;
    private final ModelMapper modelMapper;
    @Autowired
    public ConsumableServiceImpl(ConsumableRepository consumableRepository, RobotRepository robotRepository, S3Service s3Service, ModelMapper modelMapper) {
        this.consumableRepository = consumableRepository;
        this.robotRepository = robotRepository;
        this.s3Service = s3Service;
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
    public void deleteConsumable(Long id) {
        consumableRepository.deleteById(id);
    }

    @Override
    public Optional<ConsumableDetailsDTO> getConsumableById(Long id) {
        Optional<Consumable> consumable = consumableRepository.findById(id);
        if (consumable.isPresent()) {
            ConsumableDetailsDTO consumableDTO = modelMapper.map(consumable.get(), ConsumableDetailsDTO.class);
            return Optional.of(consumableDTO);
        }
        return Optional.empty();
    }


    @Override
    public void uploadConsumableImage(Long consumableId, List<MultipartFile> files) throws IOException {
        Optional<Consumable> consumableOptional = consumableRepository.findById(consumableId);
        if (consumableOptional.isEmpty()) {
            throw new IllegalArgumentException("Consumable with ID " + consumableId + " does not exist.");
        }

        Consumable consumable = consumableOptional.get();

        // Step 1: Delete existing images from S3
        for (String imageUrl : consumable.getImages()) {
            String objectKey = imageUrl.replace("https://robot-review-robot-images.s3.eu-central-1.amazonaws.com/", "");
            s3Service.deleteObjectFromBucket("robot-review-robot-images", objectKey);
        }

        // Step 2: Clear the image list in the Consumable object
        consumable.getImages().clear();
        consumableRepository.save(consumable);  // Persist the empty image list

        // Step 3: Upload new images (preserving order)
        List<String> uploadedImageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String extension = FileUtils.getExtensionOfFile(file);
            String contentType = FileUtils.determineContentType(extension);

            if (contentType.isEmpty()) {
                throw new IllegalArgumentException("Unsupported file type.");
            }

            String uniqueId = UUID.randomUUID().toString();
            String objectKey = String.format("consumables/%s/Consumable%s_%s.%s",
                    consumableId, consumableId, uniqueId, extension);

            s3Service.putObject(
                    "robot-review-robot-images",
                    objectKey,
                    file.getBytes(),
                    contentType
            );

            String imageUrl = "https://robot-review-robot-images.s3.eu-central-1.amazonaws.com/" + objectKey;
            uploadedImageUrls.add(imageUrl);  // Add in exact order
        }

        // Step 4: Save URLs to consumable (preserving the upload order)
        consumable.setImages(uploadedImageUrls);
        consumableRepository.save(consumable);  // Persist new list
    }
}
