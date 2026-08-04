package com.geoqueryai.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.geoqueryai.backend.dto.CreateParcelRequest;
import com.geoqueryai.backend.dto.ParcelResponse;
import com.geoqueryai.backend.entity.Parcel;
import com.geoqueryai.backend.service.ParcelService;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    // Create a parcel using latitude and longitude
    @PostMapping
    public ResponseEntity<ParcelResponse> createParcel(
            @RequestBody CreateParcelRequest request) {

        ParcelResponse response =
                parcelService.createParcelResponse(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all parcels
    @GetMapping
    public ResponseEntity<List<Parcel>> getAllParcels() {

        return ResponseEntity.ok(
                parcelService.getAllParcels()
        );
    }

    // Find parcels near a coordinate
    @GetMapping("/nearby")
    public ResponseEntity<List<ParcelResponse>> findNearbyParcels(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double distance) {

        List<ParcelResponse> parcels =
                parcelService.findNearbyParcels(
                        latitude,
                        longitude,
                        distance
                );

        return ResponseEntity.ok(parcels);
    }

    // Get one parcel by ID
    @GetMapping("/{id}")
    public ResponseEntity<Parcel> getParcelById(
            @PathVariable Long id) {

        return parcelService.getParcelById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // Update a parcel
    @PutMapping("/{id}")
    public ResponseEntity<Parcel> updateParcel(
            @PathVariable Long id,
            @RequestBody Parcel updatedParcel) {

        Parcel savedParcel =
                parcelService.updateParcel(id, updatedParcel);

        return ResponseEntity.ok(savedParcel);
    }

    // Delete a parcel
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(
            @PathVariable Long id) {

        parcelService.deleteParcel(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}