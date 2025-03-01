package com.robobg.service;

import com.robobg.entity.dtos.ConsumableDTO.ConsumableListDTO;
import com.robobg.entity.dtos.ConsumableDTO.CreateConsumableDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ConsumableService {

    List<ConsumableListDTO> getAllConsumables();
    void createConsumableService(CreateConsumableDTO createConsumableDTO);
    void updateConsumable(CreateConsumableDTO updateConsumableDTO);

    void deleteConsumable(Long id);

}
