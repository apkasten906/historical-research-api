package com.historicalrpg.research.repository;

import com.historicalrpg.research.entity.ClaimEvidence;
import com.historicalrpg.research.entity.ClaimEvidenceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimEvidenceRepository extends JpaRepository<ClaimEvidence, ClaimEvidenceId> {
}
