package com.web.bookingservice.constant;

public enum PaymentStatus {
    PENDING("PENDING"),
    PAID("PAID"),
    FAILED("FAILED");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
