package com.geoqueryai.backend.exception;

public class ParcelNotFoundException extends RuntimeException {

    public ParcelNotFoundException(Long id) {
        super("Parcel not found with id: " + id);
    }
}