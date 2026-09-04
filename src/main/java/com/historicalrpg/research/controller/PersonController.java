package com.historicalrpg.research.controller;

import com.historicalrpg.research.dto.PersonResponse;
import com.historicalrpg.research.repository.PersonRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/people")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public List<PersonResponse> findAll() {
        return personRepository.findAll().stream()
                .map(PersonResponse::fromEntity)
                .toList();
    }
}
