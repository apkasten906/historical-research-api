package com.historicalrpg.research.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.historicalrpg.research.dto.ResearchItemRequest;
import com.historicalrpg.research.dto.ResearchItemResponse;
import com.historicalrpg.research.entity.ResearchStatus;
import com.historicalrpg.research.exception.ResearchItemCodeMismatchException;
import com.historicalrpg.research.service.ResearchItemService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ResearchItemController.class)
class ResearchItemControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ResearchItemService researchItemService;

        @Test
        void returnsResearchItemByCode() throws Exception {
                when(researchItemService.findByCode("RES-095")).thenReturn(new ResearchItemResponse(
                                1L,
                                "RES-095",
                                "Thomas Everingham",
                                ResearchStatus.IN_PROGRESS,
                                null,
                                Instant.parse("2026-01-01T00:00:00Z"),
                                Instant.parse("2026-01-01T00:00:00Z")));

                mockMvc.perform(get("/api/research-items/RES-095"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value("RES-095"))
                                .andExpect(jsonPath("$.title").value("Thomas Everingham"));
        }

        @Test
        void createsResearchItem() throws Exception {
                ResearchItemRequest request = new ResearchItemRequest(
                                "RES-200",
                                "New lead",
                                ResearchStatus.OPEN,
                                null);
                when(researchItemService.create(request)).thenReturn(new ResearchItemResponse(
                                10L,
                                "RES-200",
                                "New lead",
                                ResearchStatus.OPEN,
                                null,
                                Instant.parse("2026-01-01T00:00:00Z"),
                                Instant.parse("2026-01-01T00:00:00Z")));

                mockMvc.perform(post("/api/research-items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "code": "RES-200",
                                                  "title": "New lead",
                                                  "status": "OPEN"
                                                }
                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(header().string("Location", containsString("/api/research-items/RES-200")))
                                .andExpect(jsonPath("$.code").value("RES-200"));
        }

        @Test
        void rejectsInvalidResearchItem() throws Exception {
                mockMvc.perform(post("/api/research-items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "code": "",
                                                  "title": "",
                                                  "status": null
                                                }
                                                """))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.validationErrors.code").exists())
                                .andExpect(jsonPath("$.validationErrors.title").exists())
                                .andExpect(jsonPath("$.validationErrors.status").exists());
        }

        @Test
        void returnsBadRequestWhenUpdateCodesDoNotMatch() throws Exception {
                when(researchItemService.update(
                                eq("RES-200"),
                                any(ResearchItemRequest.class)))
                                .thenThrow(new ResearchItemCodeMismatchException(
                                                "RES-200",
                                                "RES-201"));

                mockMvc.perform(put("/api/research-items/RES-200")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "code": "RES-201",
                                                  "title": "Updated title",
                                                  "status": "COMPLETE"
                                                }
                                                """))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error")
                                                .value("Path code and request body code must match"));
        }
}
