package com.geoqueryai.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Create Parcel
    @PostMapping
    public ResponseEntity<ParcelResponse> createParcel(
            @RequestBody CreateParcelRequest request) {

        ParcelResponse response = parcelService.createParcelResponse(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get All Parcels
    @GetMapping
    public ResponseEntity<List<Parcel>> getAllParcels() {
        return ResponseEntity.ok(parcelService.getAllParcels());
    }

    // Get Parcel by ID
    @GetMapping("/{id}")
    public ResponseEntity<Parcel> getParcelById(@PathVariable Long id) {

        return parcelService.getParcelById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update Parcel
    @PutMapping("/{id}")
    public ResponseEntity<Parcel> updateParcel(
            @PathVariable Long id,
            @RequestBody Parcel updatedParcel) {

        Parcel savedParcel = parcelService.updateParcel(id, updatedParcel);

        return ResponseEntity.ok(savedParcel);
    }

    // Delete Parcel
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {

        parcelService.deleteParcel(id);

        return ResponseEntity.noContent().build();
    }
}