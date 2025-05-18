package com.robobg.service;

import com.robobg.entity.dtos.ConsumableDTO.ConsumableDetailsDTO;
import com.robobg.entity.dtos.ConsumableDTO.ConsumableListDTO;
import com.robobg.entity.dtos.ConsumableDTO.CreateConsumableDTO;
import com.robobg.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public interface ConsumableService {

    List<ConsumableListDTO> getAllConsumables();
    void createConsumableService(CreateConsumableDTO createConsumableDTO);
    void updateConsumable(CreateConsumableDTO updateConsumableDTO);

    void deleteConsumable(Long id) throws EntityNotFoundException;

    Optional<ConsumableDetailsDTO> getConsumableById(Long id);

    void uploadConsumableImage(Long consumableId, List<MultipartFile> file) throws IOException;
}
