package com.historicalrpg.research.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "claim")
public class Claim extends KnowledgeRecord {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_entity_id", nullable = false)
    private KnowledgeEntity subject;

    @Column(nullable = false, length = 100)
    private String predicate;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "object_entity_id")
    private KnowledgeEntity object;

    @Column(columnDefinition = "text")
    private String literalValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ClaimType claimType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EvidenceStatus evidenceStatus;

    private LocalDate validFrom;

    private LocalDate validTo;

    @Column(length = 50)
    private String datePrecision;

    @Column(columnDefinition = "text")
    private String dateNote;

    @Column(columnDefinition = "text")
    private String notes;

    @OneToMany(mappedBy = "claim")
    @OrderBy("citation.id ASC, evidenceType ASC")
    private Set<ClaimEvidence> evidence = new LinkedHashSet<>();

    public Set<ClaimEvidence> getEvidence() { return evidence; }

    public KnowledgeEntity getSubject() { return subject; }
    public void setSubject(KnowledgeEntity subject) { this.subject = subject; }

    public String getPredicate() { return predicate; }
    public void setPredicate(String predicate) { this.predicate = predicate; }

    public KnowledgeEntity getObject() { return object; }
    public void setObject(KnowledgeEntity object) { this.object = object; }

    public String getLiteralValue() { return literalValue; }
    public void setLiteralValue(String literalValue) { this.literalValue = literalValue; }

    public ClaimType getClaimType() { return claimType; }
    public void setClaimType(ClaimType claimType) { this.claimType = claimType; }

    public EvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public void setEvidenceStatus(EvidenceStatus evidenceStatus) { this.evidenceStatus = evidenceStatus; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }

    public String getDatePrecision() { return datePrecision; }
    public void setDatePrecision(String datePrecision) { this.datePrecision = datePrecision; }

    public String getDateNote() { return dateNote; }
    public void setDateNote(String dateNote) { this.dateNote = dateNote; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
