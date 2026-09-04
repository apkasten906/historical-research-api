package com.historicalrpg.research.dto;

import com.historicalrpg.research.entity.ResearchItem;
import com.historicalrpg.research.entity.ResearchStatus;
import java.time.Instant;

public record ResearchItemResponse(
        Long id,
        String code,
        String title,
        ResearchStatus status,
        String summary,
        Instant createdAt,
        Instant updatedAt) {

    public static ResearchItemResponse fromEntity(ResearchItem item) {
        return new ResearchItemResponse(
                item.getId(),
                item.getCode(),
                item.getTitle(),
                item.getStatus(),
                item.getSummary(),
                item.getCreatedAt(),
                item.getUpdatedAt());
    }
}
