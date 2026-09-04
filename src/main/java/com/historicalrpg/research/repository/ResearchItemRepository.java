package com.historicalrpg.research.repository;

import com.historicalrpg.research.entity.ResearchItem;
import com.historicalrpg.research.entity.ResearchStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchItemRepository extends JpaRepository<ResearchItem, Long> {

    Optional<ResearchItem> findByCode(String code);

    List<ResearchItem> findByStatus(ResearchStatus status);
}
