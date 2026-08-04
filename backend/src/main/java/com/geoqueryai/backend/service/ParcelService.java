package com.geoqueryai.backend.service;

import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import com.geoqueryai.backend.dto.CreateParcelRequest;
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

    public Parcel createParcel(CreateParcelRequest request) {

        Coordinate coordinate = new Coordinate(
                request.getLongitude(),
                request.getLatitude()
        );

        Point location = geometryFactory.createPoint(coordinate);
        location.setSRID(4326);

        Parcel parcel = new Parcel();
        parcel.setOwnerName(request.getOwnerName());
        parcel.setAddress(request.getAddress());
        parcel.setArea(request.getArea());
        parcel.setLocation(location);

        return parcelRepository.save(parcel);
    }

    public ParcelResponse createParcelResponse(CreateParcelRequest request) {

        Parcel savedParcel = createParcel(request);

        return toResponse(savedParcel);
    }

    public Parcel saveParcel(Parcel parcel) {
        return parcelRepository.save(parcel);
    }

    public List<Parcel> getAllParcels() {
        return parcelRepository.findAll();
    }

    public Optional<Parcel> getParcelById(Long id) {
        return parcelRepository.findById(id);
    }

    public Parcel updateParcel(Long id, Parcel updatedParcel) {

        return parcelRepository.findById(id)
                .map(existingParcel -> {

                    existingParcel.setOwnerName(updatedParcel.getOwnerName());
                    existingParcel.setAddress(updatedParcel.getAddress());
                    existingParcel.setArea(updatedParcel.getArea());

                    if (updatedParcel.getLocation() != null) {
                        existingParcel.setLocation(updatedParcel.getLocation());
                    }

                    return parcelRepository.save(existingParcel);
                })
                .orElseThrow(() -> new ParcelNotFoundException(id));
    }

    public void deleteParcel(Long id) {

        if (!parcelRepository.existsById(id)) {
            throw new ParcelNotFoundException(id);
        }

        parcelRepository.deleteById(id);
    }

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