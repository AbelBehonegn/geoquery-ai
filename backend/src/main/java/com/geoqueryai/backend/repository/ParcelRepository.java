package com.geoqueryai.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.geoqueryai.backend.entity.Parcel;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    // =========================
    // FIND NEARBY PARCELS
    // =========================
    // Finds parcels whose location point is within
    // a given distance from the supplied coordinate.
    //
    // We cast geometry to geography so the
    // distance is interpreted in meters.
    @Query(
        value = """
            SELECT *
            FROM parcels p
            WHERE p.location IS NOT NULL
              AND ST_DWithin(
                    p.location::geography,
                    ST_SetSRID(
                        ST_MakePoint(:longitude, :latitude),
                        4326
                    )::geography,
                    :distanceMeters
              )
            """,
        nativeQuery = true
    )
    List<Parcel> findParcelsNearby(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("distanceMeters") Double distanceMeters
    );


    // =========================
    // FIND PARCEL CONTAINING POINT
    // =========================
    // Checks which polygon boundary contains
    // a specific longitude/latitude point.
    @Query(
        value = """
            SELECT *
            FROM parcels p
            WHERE p.boundary IS NOT NULL
              AND ST_Contains(
                    p.boundary,
                    ST_SetSRID(
                        ST_MakePoint(:longitude, :latitude),
                        4326
                    )
              )
            LIMIT 1
            """,
        nativeQuery = true
    )
    Optional<Parcel> findParcelContainingPoint(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude
    );


    // =========================
    // CALCULATE DISTANCE
    // =========================
    // Calculates the distance between
    // two parcel location points.
    //
    // geography makes PostGIS return
    // the result in meters.
    @Query(
        value = """
            SELECT ST_Distance(
                first_parcel.location::geography,
                second_parcel.location::geography
            )
            FROM parcels first_parcel
            CROSS JOIN parcels second_parcel
            WHERE first_parcel.id = :fromId
              AND second_parcel.id = :toId
              AND first_parcel.location IS NOT NULL
              AND second_parcel.location IS NOT NULL
            """,
        nativeQuery = true
    )
    Double calculateDistanceBetweenParcels(
            @Param("fromId") Long fromId,
            @Param("toId") Long toId
    );


    // =========================
    // CALCULATE PARCEL AREA
    // =========================
    // Calculates the polygon area.
    //
    // Our polygons use SRID 4326,
    // which stores coordinates in degrees.
    //
    // Casting to geography allows PostGIS
    // to calculate a real-world area
    // in square meters.
    @Query(
        value = """
            SELECT ST_Area(
                p.boundary::geography
            )
            FROM parcels p
            WHERE p.id = :id
              AND p.boundary IS NOT NULL
            """,
        nativeQuery = true
    )
    Double calculateParcelArea(
            @Param("id") Long id
    );
}