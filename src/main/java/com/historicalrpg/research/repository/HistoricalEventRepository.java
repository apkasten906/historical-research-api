package com.historicalrpg.research.repository;

import com.historicalrpg.research.entity.HistoricalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricalEventRepository extends JpaRepository<HistoricalEvent, Long> {
}
