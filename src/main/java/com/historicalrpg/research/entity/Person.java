package com.historicalrpg.research.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer bornYear;

    private Integer diedYear;

    @Column(nullable = false)
    private boolean historical;

    @Column(columnDefinition = "text")
    private String notes;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getBornYear() {
        return bornYear;
    }

    public Integer getDiedYear() {
        return diedYear;
    }

    public boolean isHistorical() {
        return historical;
    }

    public String getNotes() {
        return notes;
    }
}
