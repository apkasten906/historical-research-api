package com.historicalrpg.research.repository;

import com.historicalrpg.research.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
