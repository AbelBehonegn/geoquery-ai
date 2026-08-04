package com.geoqueryai.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.geoqueryai.backend.entity.Parcel;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    @Query(value = """
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
            """, nativeQuery = true)
    List<Parcel> findParcelsNearby(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("distanceMeters") Double distanceMeters);

}