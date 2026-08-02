package com.geoqueryai.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geoqueryai.backend.dto.ParcelDTO;
import com.geoqueryai.backend.service.ParcelService;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @GetMapping("/sample")
    public ParcelDTO getSampleParcel() {
        return parcelService.getSampleParcel();
    }
}