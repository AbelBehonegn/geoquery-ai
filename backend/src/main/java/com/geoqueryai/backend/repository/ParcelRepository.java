package com.geoqueryai.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.geoqueryai.backend.entity.Parcel;

public interface ParcelRepository
        extends JpaRepository<Parcel, Long> {


    // =========================
    // FIND NEARBY PARCELS
    // =========================
    // Finds parcels whose location point
    // is within the requested distance.
    //
    // Longitude = X
    // Latitude  = Y
    //
    // Geography allows distance
    // calculations in meters.
    @Query(
        value = """
            SELECT *
            FROM parcels p
            WHERE p.location IS NOT NULL
              AND ST_DWithin(
                    p.location::geography,
                    ST_SetSRID(
                        ST_MakePoint(
                            :longitude,
                            :latitude
                        ),
                        4326
                    )::geography,
                    :distanceMeters
              )
            """,
        nativeQuery = true
    )
    List<Parcel> findParcelsNearby(

            @Param("latitude")
            Double latitude,

            @Param("longitude")
            Double longitude,

            @Param("distanceMeters")
            Double distanceMeters
    );


    // =========================
    // FIND PARCEL CONTAINING POINT
    // =========================
    // Finds the parcel polygon that
    // covers the supplied coordinate.
    //
    // ST_Covers is used instead of
    // ST_Contains because map clicks
    // can sometimes fall directly
    // on the polygon boundary.
    //
    // ST_Covers returns TRUE when
    // the point is:
    //
    // - inside the polygon
    // - OR on the polygon boundary
    //
    // Longitude = X
    // Latitude  = Y
    @Query(
        value = """
            SELECT *
            FROM parcels p
            WHERE p.boundary IS NOT NULL
              AND ST_Covers(
                    p.boundary,
                    ST_SetSRID(
                        ST_MakePoint(
                            :longitude,
                            :latitude
                        ),
                        4326
                    )
              )
            LIMIT 1
            """,
        nativeQuery = true
    )
    Optional<Parcel> findParcelContainingPoint(

            @Param("latitude")
            Double latitude,

            @Param("longitude")
            Double longitude
    );


    // =========================
    // CALCULATE DISTANCE
    // =========================
    // Calculates the distance
    // between two parcel location
    // points.
    //
    // Result is returned in meters.
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

            @Param("fromId")
            Long fromId,

            @Param("toId")
            Long toId
    );


    // =========================
    // CALCULATE PARCEL AREA
    // =========================
    // Calculates polygon area.
    //
    // Boundary uses SRID 4326.
    //
    // Casting geometry to geography
    // makes PostGIS calculate the
    // real-world area in square meters.
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

            @Param("id")
            Long id
    );
}