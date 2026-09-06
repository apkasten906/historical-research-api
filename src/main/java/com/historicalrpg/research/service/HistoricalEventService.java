package com.historicalrpg.research.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.historicalrpg.research.dto.HistoricalEventResponse;
import com.historicalrpg.research.repository.HistoricalEventRepository;

@Service
public class HistoricalEventService {

    private final HistoricalEventRepository repository;

    public HistoricalEventService(
            HistoricalEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<HistoricalEventResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(HistoricalEventResponse::fromEntity)
                .toList();
    }
}
