package com.historicalrpg.research.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.historicalrpg.research.dto.ResearchItemRequest;
import com.historicalrpg.research.dto.ResearchItemResponse;
import com.historicalrpg.research.entity.ResearchItem;
import com.historicalrpg.research.entity.ResearchStatus;
import com.historicalrpg.research.repository.ResearchItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ResearchItemServiceTest {

    private final ResearchItemRepository repository = Mockito.mock(ResearchItemRepository.class);
    private final ResearchItemService service = new ResearchItemService(repository);

    @Test
    void updatesExistingResearchItem() {
        ResearchItem item = new ResearchItem();
        item.setCode("RES-200");
        item.setTitle("Old title");
        item.setStatus(ResearchStatus.OPEN);
        when(repository.findByCode("RES-200")).thenReturn(Optional.of(item));

        ResearchItemRequest request = new ResearchItemRequest(
                "RES-200",
                "Updated title",
                ResearchStatus.COMPLETE,
                "Reviewed notes");

        ResearchItemResponse response = service.update("RES-200", request);

        assertThat(response.title()).isEqualTo("Updated title");
        assertThat(response.status()).isEqualTo(ResearchStatus.COMPLETE);
        assertThat(item.getSummary()).isEqualTo("Reviewed notes");
        verify(repository).findByCode("RES-200");
    }
}
