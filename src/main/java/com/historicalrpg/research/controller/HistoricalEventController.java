package com.historicalrpg.research.controller;

import com.historicalrpg.research.dto.HistoricalEventResponse;
import com.historicalrpg.research.repository.HistoricalEventRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class HistoricalEventController {

    private final HistoricalEventRepository historicalEventRepository;

    public HistoricalEventController(HistoricalEventRepository historicalEventRepository) {
        this.historicalEventRepository = historicalEventRepository;
    }

    @GetMapping
    public List<HistoricalEventResponse> findAll() {
        return historicalEventRepository.findAll().stream()
                .map(HistoricalEventResponse::fromEntity)
                .toList();
    }
}
