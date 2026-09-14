package com.historicalrpg.research.repository;

import com.historicalrpg.research.entity.Citation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitationRepository extends JpaRepository<Citation, Long> {
}
