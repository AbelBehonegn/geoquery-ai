package com.geoqueryai.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.geoqueryai.backend.entity.Parcel;
import com.geoqueryai.backend.exception.ParcelNotFoundException;
import com.geoqueryai.backend.repository.ParcelRepository;

@Service
public class ParcelService {

    private final ParcelRepository parcelRepository;

    public ParcelService(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
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
}