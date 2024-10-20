package com.robobg.service;

import com.robobg.entity.dtos.RobotDTO.CreatePurchaseLinkDTO;
import org.springframework.stereotype.Service;

@Service
public interface PurchaseLinkService {
    void createPurchaseLink(CreatePurchaseLinkDTO createPurchaseLinkDTO);
    void deletePurchaseLink(Long id);
}
