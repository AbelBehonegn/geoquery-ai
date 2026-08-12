package com.geoqueryai.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.geoqueryai.backend.dto.DistanceResponse;
import com.geoqueryai.backend.dto.GeoJsonFeatureCollection;
import com.geoqueryai.backend.dto.ParcelResponse;
import com.geoqueryai.backend.dto.UpdateParcelBoundaryRequest;
import com.geoqueryai.backend.entity.Parcel;
import com.geoqueryai.backend.service.ParcelService;


// =========================
// ALLOW REACT FRONTEND
// =========================
// React runs on port 5173.
// Spring Boot runs on port 8080.
// This allows the frontend to call the backend.
@CrossOrigin(origins = "http://localhost:5173")


// =========================
// REST CONTROLLER
// =========================
@RestController

// All endpoints in this controller
// start with /api/parcels
@RequestMapping("/api/parcels")
public class ParcelController {


    // =========================
    // SERVICE
    // =========================
    private final ParcelService parcelService;


    // =========================
    // CONSTRUCTOR INJECTION
    // =========================
    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }


    // =========================
    // CREATE PARCEL
    // =========================
    // POST /api/parcels
    //
    // Creates a new parcel with:
    // - owner
    // - address
    // - area
    // - point location
    // - optional polygon boundary
    @PostMapping
    public ResponseEntity<ParcelResponse> createParcel(
            @RequestBody CreateParcelRequest request) {

        ParcelResponse response =
                parcelService.createParcelResponse(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================
    // GET ALL PARCELS
    // =========================
    // GET /api/parcels
    @GetMapping
    public ResponseEntity<List<Parcel>> getAllParcels() {

        return ResponseEntity.ok(
                parcelService.getAllParcels()
        );
    }


    // =========================
    // GET PARCELS AS GEOJSON
    // =========================
    // GET /api/parcels/geojson
    //
    // Used by React + Leaflet
    // to draw points and polygons.
    @GetMapping("/geojson")
    public ResponseEntity<GeoJsonFeatureCollection>
            getParcelsAsGeoJson() {

        GeoJsonFeatureCollection response =
                parcelService.getParcelsAsGeoJson();

        return ResponseEntity.ok(response);
    }


    // =========================
    // NEARBY SEARCH
    // =========================
    // GET /api/parcels/nearby
    //
    // Example:
    // /api/parcels/nearby
    // ?latitude=39.084
    // &longitude=-77.1528
    // &distance=500
    @GetMapping("/nearby")
    public ResponseEntity<List<ParcelResponse>>
            findNearbyParcels(

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


    // =========================
    // POINT-IN-POLYGON SEARCH
    // =========================
    // GET /api/parcels/contains
    //
    // Finds which parcel polygon
    // contains a coordinate.
    @GetMapping("/contains")
    public ResponseEntity<ParcelResponse>
            findParcelContainingPoint(

                    @RequestParam Double latitude,
                    @RequestParam Double longitude) {

        return parcelService
                .findParcelContainingPoint(
                        latitude,
                        longitude
                )
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }


    // =========================
    // DISTANCE BETWEEN PARCELS
    // =========================
    // GET /api/parcels/distance
    //
    // Example:
    // /api/parcels/distance
    // ?fromId=3
    // &toId=5
    @GetMapping("/distance")
    public ResponseEntity<DistanceResponse>
            calculateDistance(

                    @RequestParam Long fromId,
                    @RequestParam Long toId) {

        DistanceResponse response =
                parcelService.calculateDistance(
                        fromId,
                        toId
                );

        return ResponseEntity.ok(response);
    }


    // =========================
    // GET PARCEL BY ID
    // =========================
    // GET /api/parcels/{id}
    //
    // Example:
    // GET /api/parcels/7
    @GetMapping("/{id}")
    public ResponseEntity<ParcelResponse>
            getParcelById(

                    @PathVariable Long id) {

        return parcelService
                .getParcelById(id)

                // Convert entity to clean DTO
                .map(parcelService::toResponse)

                .map(ResponseEntity::ok)

                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }


    // =========================
    // UPDATE PARCEL ATTRIBUTES
    // =========================
    // PUT /api/parcels/{id}
    //
    // Updates:
    // - ownerName
    // - address
    // - area
    //
    // Existing geometry remains unchanged
    // unless geometry is explicitly supplied.
    @PutMapping("/{id}")
    public ResponseEntity<ParcelResponse> updateParcel(
            @PathVariable Long id,
            @RequestBody Parcel updatedParcel) {

        Parcel savedParcel =
                parcelService.updateParcel(
                        id,
                        updatedParcel
                );

        // Convert entity to DTO
        // so JTS geometry is not
        // serialized directly.
        ParcelResponse response =
                parcelService.toResponse(savedParcel);

        return ResponseEntity.ok(response);
    }


    // =========================
    // UPDATE PARCEL BOUNDARY
    // =========================
    // PUT /api/parcels/{id}/boundary
    //
    // Used when a user reshapes
    // a polygon in Leaflet.
    //
    // This updates only the polygon,
    // without changing the parcel's
    // owner, address, or location.
    @PutMapping("/{id}/boundary")
    public ResponseEntity<ParcelResponse>
            updateParcelBoundary(

                    @PathVariable Long id,

                    @RequestBody
                    UpdateParcelBoundaryRequest request) {

        ParcelResponse response =
                parcelService.updateParcelBoundary(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // =========================
    // DELETE PARCEL
    // =========================
    // DELETE /api/parcels/{id}
    //
    // Example:
    // DELETE /api/parcels/7
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
            deleteParcel(

                    @PathVariable Long id) {

        parcelService.deleteParcel(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}