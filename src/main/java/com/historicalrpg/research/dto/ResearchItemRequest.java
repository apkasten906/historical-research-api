package com.historicalrpg.research.dto;

import com.historicalrpg.research.entity.ResearchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResearchItemRequest(
        @NotBlank String code,
        @NotBlank String title,
        @NotNull ResearchStatus status,
        String summary) {
}
