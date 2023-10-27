package com.rbp.bookmymovie.exception;

public class SeatAlreadyBooked extends RuntimeException {
    public SeatAlreadyBooked(String s) {
        super(s);
    }
}
