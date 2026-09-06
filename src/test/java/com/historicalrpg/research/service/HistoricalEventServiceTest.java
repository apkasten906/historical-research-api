package com.historicalrpg.research.service;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.historicalrpg.research.dto.HistoricalEventResponse;
import com.historicalrpg.research.entity.HistoricalEvent;
import com.historicalrpg.research.entity.Location;
import com.historicalrpg.research.repository.HistoricalEventRepository;

@ExtendWith(MockitoExtension.class)
class HistoricalEventServiceTest {

    @Mock
    private HistoricalEventRepository historicalEventRepository;

    @InjectMocks
    private HistoricalEventService historicalEventService;

    @Test
    void findAllReturnsMappedHistoricalEvents() {
        
        // Arrange
        Location bruges = new Location();
        bruges.setName("Bruges");

        HistoricalEvent event = new HistoricalEvent();
        event.setId(1L);
        event.setTitle("Bruges research context");
        event.setLocation(bruges);

        // Act
        when(historicalEventRepository.findAll())
                .thenReturn(List.of(event));

        List<HistoricalEventResponse> result =
                historicalEventService.findAll();

        // Assert
        assertThat(result).hasSize(1);

        assertThat(result.get(0).title())
                .isEqualTo("Bruges research context");

        assertThat(result.get(0).locationName())
                .isEqualTo("Bruges");

        // Verify
        verify(historicalEventRepository).findAll();
    }
}