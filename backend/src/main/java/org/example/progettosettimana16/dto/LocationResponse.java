package org.example.progettosettimana16.dto;

import org.example.progettosettimana16.entity.Location;

public record LocationResponse(Double latitude, Double longitude, String address) {

    public static LocationResponse from(Location location) {
        return location == null ? null
                : new LocationResponse(location.getLatitude(), location.getLongitude(), location.getAddress());
    }
}
