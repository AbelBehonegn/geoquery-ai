package com.geoqueryai.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.geoqueryai.backend.entity.Parcel;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    // Find parcels within a distance measured in meters
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

    // Find the parcel polygon that contains a coordinate
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

    // Calculate distance in meters between two parcel location points
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
}