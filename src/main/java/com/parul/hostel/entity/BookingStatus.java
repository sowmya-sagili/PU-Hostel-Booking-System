package com.parul.hostel.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum BookingStatus {
    PENDING("pending"),
    APPROVED("approved"),
    CANCELLED("cancelled");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Converter(autoApply = true)
    public static class ConverterImpl implements AttributeConverter<BookingStatus, String> {
        @Override
        public String convertToDatabaseColumn(BookingStatus attribute) {
            return attribute != null ? attribute.getValue() : null;
        }

        @Override
        public BookingStatus convertToEntityAttribute(String dbData) {
            if (dbData == null) {
                return null;
            }
            for (BookingStatus status : BookingStatus.values()) {
                if (status.getValue().equalsIgnoreCase(dbData)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown database value: " + dbData);
        }
    }
}
