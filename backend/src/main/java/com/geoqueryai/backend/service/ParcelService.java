package com.geoqueryai.backend.service;

import org.springframework.stereotype.Service;

import com.geoqueryai.backend.dto.ParcelDTO;

@Service
public class ParcelService {

    public ParcelDTO getSampleParcel() {

        return new ParcelDTO(
                "John Smith",
                "123 Main Street",
                2500.0
        );
    }
}