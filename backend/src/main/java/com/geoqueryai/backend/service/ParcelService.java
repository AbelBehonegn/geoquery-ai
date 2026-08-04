package com.geoqueryai.backend.service;

import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import com.geoqueryai.backend.dto.CreateParcelRequest;
import com.geoqueryai.backend.dto.DistanceResponse;
import com.geoqueryai.backend.dto.ParcelResponse;
import com.geoqueryai.backend.entity.Parcel;
import com.geoqueryai.backend.exception.ParcelNotFoundException;
import com.geoqueryai.backend.repository.ParcelRepository;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;

    private final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    public ParcelService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    // Create a parcel with a Point and an optional Polygon boundary
    public Parcel createParcel(CreateParcelRequest request) {

        Coordinate locationCoordinate = new Coordinate(
                request.getLongitude(),
                request.getLatitude()
        );

        Point location = geometryFactory.createPoint(locationCoordinate);
        location.setSRID(4326);

        Parcel parcel = new Parcel();
        parcel.setOwnerName(request.getOwnerName());
        parcel.setAddress(request.getAddress());
        parcel.setArea(request.getArea());
        parcel.setLocation(location);

        if (request.getBoundary() != null
                && request.getBoundary().size() >= 4) {

            Coordinate[] boundaryCoordinates =
                    new Coordinate[request.getBoundary().size()];

            for (int i = 0; i < request.getBoundary().size(); i++) {

                List<Double> pair = request.getBoundary().get(i);

                boundaryCoordinates[i] = new Coordinate(
                        pair.get(0), // longitude
                        pair.get(1)  // latitude
                );
            }

            LinearRing shell =
                    geometryFactory.createLinearRing(boundaryCoordinates);

            Polygon boundary =
                    geometryFactory.createPolygon(shell);

            boundary.setSRID(4326);

            parcel.setBoundary(boundary);
        }

        return parcelRepository.save(parcel);
    }

    // Create a parcel and return a clean response DTO
    public ParcelResponse createParcelResponse(
            CreateParcelRequest request) {

        Parcel savedParcel = createParcel(request);

        return toResponse(savedParcel);
    }

    // Save a parcel
    public Parcel saveParcel(Parcel parcel) {
        return parcelRepository.save(parcel);
    }

    // Get all parcels
    public List<Parcel> getAllParcels() {
        return parcelRepository.findAll();
    }

    // Get one parcel by ID
    public Optional<Parcel> getParcelById(Long id) {
        return parcelRepository.findById(id);
    }

    // Update a parcel
    public Parcel updateParcel(
            Long id,
            Parcel updatedParcel) {

        return parcelRepository.findById(id)
                .map(existingParcel -> {

                    existingParcel.setOwnerName(
                            updatedParcel.getOwnerName()
                    );

                    existingParcel.setAddress(
                            updatedParcel.getAddress()
                    );

                    existingParcel.setArea(
                            updatedParcel.getArea()
                    );

                    if (updatedParcel.getLocation() != null) {
                        existingParcel.setLocation(
                                updatedParcel.getLocation()
                        );
                    }

                    if (updatedParcel.getBoundary() != null) {
                        existingParcel.setBoundary(
                                updatedParcel.getBoundary()
                        );
                    }

                    return parcelRepository.save(existingParcel);
                })
                .orElseThrow(
                        () -> new ParcelNotFoundException(id)
                );
    }

    // Delete a parcel
    public void deleteParcel(Long id) {

        if (!parcelRepository.existsById(id)) {
            throw new ParcelNotFoundException(id);
        }

        parcelRepository.deleteById(id);
    }

    // Find parcels near a coordinate
    public List<ParcelResponse> findNearbyParcels(
            Double latitude,
            Double longitude,
            Double distanceMeters) {

        List<Parcel> parcels =
                parcelRepository.findParcelsNearby(
                        latitude,
                        longitude,
                        distanceMeters
                );

        return parcels.stream()
                .map(this::toResponse)
                .toList();
    }

    // Find the parcel polygon containing a coordinate
    public Optional<ParcelResponse> findParcelContainingPoint(
            Double latitude,
            Double longitude) {

        return parcelRepository
                .findParcelContainingPoint(
                        latitude,
                        longitude
                )
                .map(this::toResponse);
    }

    // Calculate distance between two parcel locations
    public DistanceResponse calculateDistance(
            Long fromId,
            Long toId) {

        Double distance =
                parcelRepository
                        .calculateDistanceBetweenParcels(
                                fromId,
                                toId
                        );

        if (distance == null) {
            throw new RuntimeException(
                    "Unable to calculate distance. "
                    + "Check that both parcels exist "
                    + "and have locations."
            );
        }

        return new DistanceResponse(
                fromId,
                toId,
                distance
        );
    }

    // Convert Parcel entity to ParcelResponse DTO
    public ParcelResponse toResponse(Parcel parcel) {

        Double latitude = null;
        Double longitude = null;

        if (parcel.getLocation() != null) {
            latitude = parcel.getLocation().getY();
            longitude = parcel.getLocation().getX();
        }

        return new ParcelResponse(
                parcel.getId(),
                parcel.getOwnerName(),
                parcel.getAddress(),
                parcel.getArea(),
                latitude,
                longitude
        );
    }
}