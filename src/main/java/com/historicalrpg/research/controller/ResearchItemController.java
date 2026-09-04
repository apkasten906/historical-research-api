package com.historicalrpg.research.controller;

import com.historicalrpg.research.dto.ResearchItemRequest;
import com.historicalrpg.research.dto.ResearchItemResponse;
import com.historicalrpg.research.service.ResearchItemService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/research-items")
public class ResearchItemController {

    private final ResearchItemService researchItemService;

    public ResearchItemController(ResearchItemService researchItemService) {
        this.researchItemService = researchItemService;
    }

    @GetMapping
    public List<ResearchItemResponse> findAll() {
        return researchItemService.findAll();
    }

    @GetMapping("/{code}")
    public ResearchItemResponse findByCode(@PathVariable String code) {
        return researchItemService.findByCode(code);
    }

    @PostMapping
    public ResponseEntity<ResearchItemResponse> create(@Valid @RequestBody ResearchItemRequest request) {
        ResearchItemResponse created = researchItemService.create(request);
        return ResponseEntity
                .created(URI.create("/api/research-items/" + created.code()))
                .body(created);
    }

    @PutMapping("/{code}")
    public ResearchItemResponse update(
            @PathVariable String code,
            @Valid @RequestBody ResearchItemRequest request) {
        return researchItemService.update(code, request);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        researchItemService.delete(code);
        return ResponseEntity.noContent().build();
    }
}
