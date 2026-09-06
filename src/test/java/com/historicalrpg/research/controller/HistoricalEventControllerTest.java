package com.historicalrpg.research.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.historicalrpg.research.dto.HistoricalEventResponse;
import com.historicalrpg.research.service.HistoricalEventService;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HistoricalEventController.class)
class HistoricalEventControllerTest {    

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private HistoricalEventService historicalEventService;

        @Test
        void findAllEventsReturns() throws Exception {
            
            HistoricalEventResponse response =
                new HistoricalEventResponse(
                    1L,
                    "Bruges research context",
                    null,
                    null,
                    "Bruges"
                );
            
            when(historicalEventService.findAll())
                    .thenReturn(List.of(response));

            mockMvc.perform(get("/api/events"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].title")
                        .value("Bruges research context"))
                    .andExpect(jsonPath("$[0].locationName")
                        .value("Bruges"));
        }
}
