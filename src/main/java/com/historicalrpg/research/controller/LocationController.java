package com.historicalrpg.research.controller;

import com.historicalrpg.research.dto.LocationResponse;
import com.historicalrpg.research.repository.LocationRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationRepository locationRepository;

    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public List<LocationResponse> findAll() {
        return locationRepository.findAll().stream()
                .map(LocationResponse::fromEntity)
                .toList();
    }
}
