package com.historicalrpg.research.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "claim_evidence")
@IdClass(ClaimEvidenceId.class)
public class ClaimEvidence {
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "citation_id", nullable = false)
    private Citation citation;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EvidenceType evidenceType;

    @Column(length = 50)
    private String strength;

    @Column(columnDefinition = "text")
    private String notes;

    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public Citation getCitation() { return citation; }
    public void setCitation(Citation citation) { this.citation = citation; }
    public EvidenceType getEvidenceType() { return evidenceType; }
    public void setEvidenceType(EvidenceType evidenceType) { this.evidenceType = evidenceType; }
    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
