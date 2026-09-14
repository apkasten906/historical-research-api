package com.historicalrpg.research.repository;

import com.historicalrpg.research.entity.KnowledgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeEntityRepository extends JpaRepository<KnowledgeEntity, Long> {
}
