package com.historicalrpg.research.repository;

import java.util.List;

import com.historicalrpg.research.entity.HistoricalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;

public interface HistoricalEventRepository extends JpaRepository<HistoricalEvent, Long> {

    @Override
    @EntityGraph(attributePaths = "location")
    // @Query("""
    //     select e
    //     from HistoricalEvent e
    //     left join fetch e.location
    //     """)
    List<HistoricalEvent> findAll();
}
