package com.historicalrpg.research.dto;

import com.historicalrpg.research.entity.Person;

public record PersonResponse(
        Long id,
        String name,
        Integer bornYear,
        Integer diedYear,
        boolean historical,
        String notes) {

    public static PersonResponse fromEntity(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getName(),
                person.getBornYear(),
                person.getDiedYear(),
                person.isHistorical(),
                person.getNotes());
    }
}
