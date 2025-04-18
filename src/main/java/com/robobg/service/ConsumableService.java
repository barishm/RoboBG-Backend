package com.robobg.service;

import com.robobg.entity.dtos.ConsumableDTO.ConsumableDetailsDTO;
import com.robobg.entity.dtos.ConsumableDTO.ConsumableListDTO;
import com.robobg.entity.dtos.ConsumableDTO.CreateConsumableDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ConsumableService {

    List<ConsumableListDTO> getAllConsumables();
    void createConsumableService(CreateConsumableDTO createConsumableDTO);
    void updateConsumable(CreateConsumableDTO updateConsumableDTO);

    void deleteConsumable(Long id);

    Optional<ConsumableDetailsDTO> getConsumableById(Long id);
}
