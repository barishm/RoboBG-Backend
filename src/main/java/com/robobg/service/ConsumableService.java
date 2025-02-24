package com.robobg.service;

import com.robobg.entity.dtos.ConsumableDTO.CreateConsumableDTO;
import org.springframework.stereotype.Service;

@Service
public interface ConsumableService {
    void createConsumableService(CreateConsumableDTO createConsumableDTO);
    void updateConsumable(Long consumableId, CreateConsumableDTO updateConsumableDTO);

}
