package com.historicalrpg.research.dto;

import com.historicalrpg.research.entity.HistoricalEvent;
import java.time.LocalDate;

public record HistoricalEventResponse(
        Long id,
        String title,
        LocalDate eventDate,
        String description,
        String locationName) {

    public static HistoricalEventResponse fromEntity(HistoricalEvent event) {
        String locationName = event.getLocation() == null ? null : event.getLocation().getName();
        return new HistoricalEventResponse(
                event.getId(),
                event.getTitle(),
                event.getEventDate(),
                event.getDescription(),
                locationName);
    }
}
