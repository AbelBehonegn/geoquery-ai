package com.geoqueryai.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.geoqueryai.backend.entity.Parcel;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {

}