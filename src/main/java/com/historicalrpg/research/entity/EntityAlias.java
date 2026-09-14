package com.historicalrpg.research.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entity_alias")
public class EntityAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entity_id", nullable = false)
    private KnowledgeEntity entity;

    @Column(nullable = false, length = 255)
    private String alias;

    @Column(length = 20)
    private String language;

    @Column(length = 50)
    private String aliasType;

    public Long getId() { return id; }

    public KnowledgeEntity getEntity() { return entity; }
    public void setEntity(KnowledgeEntity entity) { this.entity = entity; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getAliasType() { return aliasType; }
    public void setAliasType(String aliasType) { this.aliasType = aliasType; }
}
