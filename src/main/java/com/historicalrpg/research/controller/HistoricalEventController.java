package com.historicalrpg.research.controller;

import com.historicalrpg.research.dto.HistoricalEventResponse;
import com.historicalrpg.research.service.HistoricalEventService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class HistoricalEventController {

    private final HistoricalEventService historicalEventService;

    public HistoricalEventController(HistoricalEventService historicalEventService) {
        this.historicalEventService = historicalEventService;
    }

    @GetMapping
    public List<HistoricalEventResponse> findAll() {
        return historicalEventService.findAll();
    }
}
