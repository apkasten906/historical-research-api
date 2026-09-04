package com.historicalrpg.research.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String locationType;

    private String modernName;

    @Column(columnDefinition = "text")
    private String notes;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getModernName() {
        return modernName;
    }

    public String getNotes() {
        return notes;
    }
}
