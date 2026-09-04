package com.historicalrpg.research.service;

import com.historicalrpg.research.dto.ResearchItemRequest;
import com.historicalrpg.research.dto.ResearchItemResponse;
import com.historicalrpg.research.entity.ResearchItem;
import com.historicalrpg.research.exception.ResearchItemCodeMismatchException;
import com.historicalrpg.research.exception.ResearchItemNotFoundException;
import com.historicalrpg.research.repository.ResearchItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResearchItemService {

    private final ResearchItemRepository researchItemRepository;

    public ResearchItemService(ResearchItemRepository researchItemRepository) {
        this.researchItemRepository = researchItemRepository;
    }

    @Transactional(readOnly = true)
    public List<ResearchItemResponse> findAll() {
        return researchItemRepository.findAll().stream()
                .map(ResearchItemResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResearchItemResponse findByCode(String code) {
        return researchItemRepository.findByCode(code)
                .map(ResearchItemResponse::fromEntity)
                .orElseThrow(() -> new ResearchItemNotFoundException(code));
    }

    @Transactional
    public ResearchItemResponse create(ResearchItemRequest request) {
        ResearchItem item = new ResearchItem();
        applyRequest(item, request);
        return ResearchItemResponse.fromEntity(researchItemRepository.save(item));
    }

    @Transactional
    public ResearchItemResponse update(String code, ResearchItemRequest request) {

        if(!code.equals(request.code())) {
            throw new ResearchItemCodeMismatchException(code, request.code());
        }
        
        ResearchItem item = researchItemRepository.findByCode(code)
                .orElseThrow(() -> new ResearchItemNotFoundException(code));
        
        applyRequest(item, request);
        return ResearchItemResponse.fromEntity(item);
    }

    @Transactional
    public void delete(String code) {
        ResearchItem item = researchItemRepository.findByCode(code)
                .orElseThrow(() -> new ResearchItemNotFoundException(code));
        researchItemRepository.delete(item);
    }

    private void applyRequest(ResearchItem item, ResearchItemRequest request) {
        item.setCode(request.code());
        item.setTitle(request.title());
        item.setStatus(request.status());
        item.setSummary(request.summary());
    }
}
