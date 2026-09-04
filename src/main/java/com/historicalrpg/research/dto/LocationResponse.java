package com.historicalrpg.research.dto;

import com.historicalrpg.research.entity.Location;

public record LocationResponse(
        Long id,
        String name,
        String locationType,
        String modernName,
        String notes) {

    public static LocationResponse fromEntity(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getLocationType(),
                location.getModernName(),
                location.getNotes());
    }
}
