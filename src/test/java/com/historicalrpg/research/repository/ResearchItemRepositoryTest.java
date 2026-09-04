package com.historicalrpg.research.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.historicalrpg.research.entity.ResearchStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ResearchItemRepositoryTest {

    @Autowired
    private ResearchItemRepository researchItemRepository;

    @Test
    void findsSeededResearchItemByCode() {
        assertThat(researchItemRepository.findByCode("RES-095"))
                .isPresent()
                .get()
                .extracting("title")
                .isEqualTo("Thomas Everingham");
    }

    @Test
    void findsSeededResearchItemsByStatus() {
        assertThat(researchItemRepository.findByStatus(ResearchStatus.OPEN))
                .extracting("code")
                .contains("RES-094", "RES-098", "RES-108");
    }
}
