package com.historicalrpg.research.repository;

import com.historicalrpg.research.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
