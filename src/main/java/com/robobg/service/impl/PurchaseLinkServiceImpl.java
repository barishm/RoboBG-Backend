package com.robobg.service.impl;

import com.robobg.entity.PurchaseLink;
import com.robobg.entity.dtos.RobotDTO.CreatePurchaseLinkDTO;
import com.robobg.repository.PurchaseLinkRepository;
import com.robobg.service.PurchaseLinkService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseLinkServiceImpl implements PurchaseLinkService {
    private final PurchaseLinkRepository purchaseLinkRepository;
    @Autowired
    private ModelMapper modelMapper;

    public PurchaseLinkServiceImpl(PurchaseLinkRepository purchaseLinkRepository) {
        this.purchaseLinkRepository = purchaseLinkRepository;
    }


    @Override
    public void createPurchaseLink(CreatePurchaseLinkDTO createPurchaseLinkDTO) {
        PurchaseLink purchaseLink = modelMapper.map(createPurchaseLinkDTO,PurchaseLink.class);
        purchaseLinkRepository.save(purchaseLink);
    }

    @Override
    public void deletePurchaseLink(Long id) {
        purchaseLinkRepository.deleteById(id);
    }
}
