package com.geoqueryai.backend.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.geoqueryai.backend.dto.GeoJsonFeature;
import com.geoqueryai.backend.dto.GeoJsonFeatureCollection;
import com.geoqueryai.backend.dto.ParcelResponse;
import com.geoqueryai.backend.dto.UpdateParcelBoundaryRequest;
import com.geoqueryai.backend.entity.Parcel;
import com.geoqueryai.backend.exception.ParcelNotFoundException;
import com.geoqueryai.backend.repository.ParcelRepository;

@Service
public class ParcelService {

    // =========================
    // REPOSITORY
    // =========================
    // Used to read and write parcel data
    // in PostgreSQL / PostGIS.
    private final ParcelRepository parcelRepository;


    // =========================
    // GEOMETRY FACTORY
    // =========================
    // Creates JTS Point, Polygon,
    // LinearRing, etc.
    //
    // SRID 4326 = longitude / latitude.
    private final GeometryFactory geometryFactory =
            new GeometryFactory(
                    new PrecisionModel(),
                    4326
            );


    // =========================
    // CONSTRUCTOR
    // =========================
    public ParcelService(
            ParcelRepository parcelRepository) {

        this.parcelRepository =
                parcelRepository;
    }


    // =========================
    // CREATE PARCEL
    // =========================
    // Converts request data into:
    //
    // - Point location
    // - Polygon boundary
    //
    // Then saves the parcel and lets
    // PostGIS calculate the real area.
    public Parcel createParcel(
            CreateParcelRequest request) {

        // =========================
        // CREATE POINT LOCATION
        // =========================
        Coordinate locationCoordinate =
                new Coordinate(
                        request.getLongitude(),
                        request.getLatitude()
                );

        Point location =
                geometryFactory.createPoint(
                        locationCoordinate
                );

        location.setSRID(4326);


        // =========================
        // CREATE PARCEL ENTITY
        // =========================
        Parcel parcel = new Parcel();

        parcel.setOwnerName(
                request.getOwnerName()
        );

        parcel.setAddress(
                request.getAddress()
        );

        // We temporarily accept the incoming area.
        // If a polygon exists, PostGIS will
        // replace this value with the
        // calculated area.
        parcel.setArea(
                request.getArea()
        );

        parcel.setLocation(location);


        // =========================
        // CREATE POLYGON BOUNDARY
        // =========================
        if (
                request.getBoundary() != null
                &&
                request.getBoundary().size() >= 4
        ) {

            Coordinate[] boundaryCoordinates =
                    new Coordinate[
                            request
                                    .getBoundary()
                                    .size()
                    ];


            // Convert:
            //
            // List<List<Double>>
            //
            // into:
            //
            // Coordinate[]
            for (
                    int i = 0;
                    i < request
                            .getBoundary()
                            .size();
                    i++
            ) {

                List<Double> pair =
                        request
                                .getBoundary()
                                .get(i);

                boundaryCoordinates[i] =
                        new Coordinate(

                                // Longitude
                                pair.get(0),

                                // Latitude
                                pair.get(1)
                        );
            }


            // =========================
            // CREATE LINEAR RING
            // =========================
            LinearRing shell =
                    geometryFactory
                            .createLinearRing(
                                    boundaryCoordinates
                            );


            // =========================
            // CREATE POLYGON
            // =========================
            Polygon boundary =
                    geometryFactory
                            .createPolygon(shell);

            boundary.setSRID(4326);

            parcel.setBoundary(boundary);
        }


        // =========================
        // SAVE + CALCULATE AREA
        // =========================
        // If a polygon exists,
        // PostGIS calculates its
        // real area in square meters.
        return calculateAndStoreArea(parcel);
    }


    // =========================
    // CREATE PARCEL RESPONSE
    // =========================
    // Creates the parcel and then
    // converts it into a clean DTO.
    public ParcelResponse createParcelResponse(
            CreateParcelRequest request) {

        Parcel savedParcel =
                createParcel(request);

        return toResponse(savedParcel);
    }


    // =========================
    // SAVE PARCEL
    // =========================
    public Parcel saveParcel(
            Parcel parcel) {

        return parcelRepository.save(parcel);
    }


    // =========================
    // GET ALL PARCELS
    // =========================
    public List<Parcel> getAllParcels() {

        return parcelRepository.findAll();
    }


    // =========================
    // GET PARCEL BY ID
    // =========================
    public Optional<Parcel> getParcelById(
            Long id) {

        return parcelRepository.findById(id);
    }


    // =========================
    // UPDATE PARCEL ATTRIBUTES
    // =========================
    // Updates:
    //
    // - owner
    // - address
    // - area
    //
    // Existing location/boundary remain
    // unchanged unless explicitly supplied.
    public Parcel updateParcel(
            Long id,
            Parcel updatedParcel) {

        return parcelRepository
                .findById(id)
                .map(existingParcel -> {

                    existingParcel
                            .setOwnerName(
                                    updatedParcel
                                            .getOwnerName()
                            );

                    existingParcel
                            .setAddress(
                                    updatedParcel
                                            .getAddress()
                            );

                    existingParcel
                            .setArea(
                                    updatedParcel
                                            .getArea()
                            );


                    // =========================
                    // OPTIONAL LOCATION UPDATE
                    // =========================
                    if (
                            updatedParcel
                                    .getLocation()
                                    != null
                    ) {

                        existingParcel
                                .setLocation(
                                        updatedParcel
                                                .getLocation()
                                );
                    }


                    // =========================
                    // OPTIONAL BOUNDARY UPDATE
                    // =========================
                    if (
                            updatedParcel
                                    .getBoundary()
                                    != null
                    ) {

                        existingParcel
                                .setBoundary(
                                        updatedParcel
                                                .getBoundary()
                                );
                    }


                    return parcelRepository
                            .save(existingParcel);
                })
                .orElseThrow(
                        () ->
                                new ParcelNotFoundException(
                                        id
                                )
                );
    }


    // =========================
    // UPDATE PARCEL BOUNDARY
    // =========================
    // Used when a user reshapes
    // an existing polygon in Leaflet.
    //
    // This updates only geometry,
    // then recalculates area.
    public ParcelResponse updateParcelBoundary(
            Long id,
            UpdateParcelBoundaryRequest request) {

        // =========================
        // FIND EXISTING PARCEL
        // =========================
        Parcel parcel =
                parcelRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ParcelNotFoundException(
                                                id
                                        )
                        );


        // =========================
        // READ NEW BOUNDARY
        // =========================
        List<List<Double>> boundary =
                request.getBoundary();


        // =========================
        // VALIDATE BOUNDARY
        // =========================
        if (
                boundary == null
                ||
                boundary.size() < 4
        ) {

            throw new IllegalArgumentException(
                    "Parcel boundary must contain at least 4 coordinates."
            );
        }


        // =========================
        // CONVERT TO JTS COORDINATES
        // =========================
        Coordinate[] coordinates =
                new Coordinate[
                        boundary.size()
                ];

        for (
                int i = 0;
                i < boundary.size();
                i++
        ) {

            List<Double> pair =
                    boundary.get(i);

            coordinates[i] =
                    new Coordinate(

                            // Longitude
                            pair.get(0),

                            // Latitude
                            pair.get(1)
                    );
        }


        // =========================
        // CREATE LINEAR RING
        // =========================
        LinearRing shell =
                geometryFactory
                        .createLinearRing(
                                coordinates
                        );


        // =========================
        // CREATE NEW POLYGON
        // =========================
        Polygon polygon =
                geometryFactory
                        .createPolygon(shell);

        polygon.setSRID(4326);


        // =========================
        // REPLACE OLD BOUNDARY
        // =========================
        parcel.setBoundary(polygon);


        // =========================
        // SAVE + RECALCULATE AREA
        // =========================
        Parcel savedParcel =
                calculateAndStoreArea(
                        parcel
                );


        return toResponse(savedParcel);
    }


    // =========================
    // DELETE PARCEL
    // =========================
    public void deleteParcel(
            Long id) {

        if (
                !parcelRepository
                        .existsById(id)
        ) {

            throw new ParcelNotFoundException(
                    id
            );
        }

        parcelRepository.deleteById(id);
    }


    // =========================
    // FIND NEARBY PARCELS
    // =========================
    public List<ParcelResponse>
            findNearbyParcels(

                    Double latitude,
                    Double longitude,
                    Double distanceMeters) {

        List<Parcel> parcels =
                parcelRepository
                        .findParcelsNearby(
                                latitude,
                                longitude,
                                distanceMeters
                        );


        return parcels
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // =========================
    // POINT-IN-POLYGON SEARCH
    // =========================
    public Optional<ParcelResponse>
            findParcelContainingPoint(

                    Double latitude,
                    Double longitude) {

        return parcelRepository
                .findParcelContainingPoint(
                        latitude,
                        longitude
                )
                .map(this::toResponse);
    }


    // =========================
    // CALCULATE DISTANCE
    // =========================
    public DistanceResponse
            calculateDistance(

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


    // =========================
    // RETURN ALL PARCELS
    // AS GEOJSON
    // =========================
    public GeoJsonFeatureCollection
            getParcelsAsGeoJson() {

        List<GeoJsonFeature> features =
                parcelRepository
                        .findAll()
                        .stream()
                        .map(this::toGeoJsonFeature)
                        .toList();


        return new GeoJsonFeatureCollection(
                features
        );
    }


    // =========================
    // CALCULATE + STORE AREA
    // =========================
    // The polygon must already exist
    // in PostgreSQL before PostGIS
    // can calculate its area.
    //
    // ST_Area(boundary::geography)
    // returns square meters.
    private Parcel calculateAndStoreArea(
            Parcel parcel) {

        // =========================
        // SAVE GEOMETRY FIRST
        // =========================
        // saveAndFlush forces Hibernate
        // to immediately write the
        // polygon to PostgreSQL.
        Parcel savedParcel =
                parcelRepository
                        .saveAndFlush(
                                parcel
                        );


        // =========================
        // NO POLYGON?
        // =========================
        // Older parcels may only have
        // a point location.
        //
        // In that situation there is
        // no polygon area to calculate.
        if (
                savedParcel
                        .getBoundary()
                        == null
        ) {

            return savedParcel;
        }


        // =========================
        // ASK POSTGIS FOR AREA
        // =========================
        Double calculatedArea =
                parcelRepository
                        .calculateParcelArea(
                                savedParcel.getId()
                        );


        // =========================
        // STORE CALCULATED AREA
        // =========================
        if (
                calculatedArea != null
        ) {

            savedParcel.setArea(
                    calculatedArea
            );


            savedParcel =
                    parcelRepository
                            .save(
                                    savedParcel
                            );
        }


        return savedParcel;
    }


    // =========================
    // CONVERT PARCEL TO DTO
    // =========================
    public ParcelResponse toResponse(
            Parcel parcel) {

        Double latitude = null;
        Double longitude = null;


        if (
                parcel.getLocation()
                        != null
        ) {

            latitude =
                    parcel
                            .getLocation()
                            .getY();

            longitude =
                    parcel
                            .getLocation()
                            .getX();
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


    // =========================
    // CONVERT PARCEL TO GEOJSON
    // =========================
    private GeoJsonFeature
            toGeoJsonFeature(
                    Parcel parcel) {

        Map<String, Object> geometry =
                new LinkedHashMap<>();

        Map<String, Object> properties =
                new LinkedHashMap<>();


        // =========================
        // POLYGON GEOMETRY
        // =========================
        if (
                parcel.getBoundary()
                        != null
        ) {

            List<List<Double>> ring =
                    new ArrayList<>();


            for (
                    Coordinate coordinate
                    :
                    parcel
                            .getBoundary()
                            .getCoordinates()
            ) {

                ring.add(
                        List.of(

                                // Longitude
                                coordinate.getX(),

                                // Latitude
                                coordinate.getY()
                        )
                );
            }


            geometry.put(
                    "type",
                    "Polygon"
            );


            geometry.put(
                    "coordinates",
                    List.of(ring)
            );
        }


        // =========================
        // POINT GEOMETRY
        // =========================
        else if (
                parcel.getLocation()
                        != null
        ) {

            geometry.put(
                    "type",
                    "Point"
            );


            geometry.put(
                    "coordinates",
                    List.of(

                            parcel
                                    .getLocation()
                                    .getX(),

                            parcel
                                    .getLocation()
                                    .getY()
                    )
            );
        }


        // =========================
        // NO GEOMETRY
        // =========================
        else {

            geometry = null;
        }


        // =========================
        // GEOJSON PROPERTIES
        // =========================
        properties.put(
                "id",
                parcel.getId()
        );

        properties.put(
                "ownerName",
                parcel.getOwnerName()
        );

        properties.put(
                "address",
                parcel.getAddress()
        );

        properties.put(
                "area",
                parcel.getArea()
        );


        return new GeoJsonFeature(
                geometry,
                properties
        );
    }
}