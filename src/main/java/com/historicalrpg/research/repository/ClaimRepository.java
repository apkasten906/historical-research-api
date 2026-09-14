package com.historicalrpg.research.repository;

import com.historicalrpg.research.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
