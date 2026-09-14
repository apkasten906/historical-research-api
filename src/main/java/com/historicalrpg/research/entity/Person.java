package com.historicalrpg.research.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

    // Populated by the database compatibility trigger; refresh after a new insert.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", insertable = false, updatable = false)
    private KnowledgeEntity knowledgeEntity;

    public KnowledgeEntity getKnowledgeEntity() { return knowledgeEntity; }

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
