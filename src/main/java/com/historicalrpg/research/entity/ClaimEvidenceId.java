package com.historicalrpg.research.entity;

import java.io.Serializable;
import java.util.Objects;

public class ClaimEvidenceId implements Serializable {
    private Long claim;
    private Long citation;
    private EvidenceType evidenceType;

    public ClaimEvidenceId() {}

    public ClaimEvidenceId(Long claim, Long citation, EvidenceType evidenceType) {
        this.claim = claim;
        this.citation = citation;
        this.evidenceType = evidenceType;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ClaimEvidenceId that)) return false;
        return Objects.equals(claim, that.claim) && Objects.equals(citation, that.citation)
                && evidenceType == that.evidenceType;
    }

    @Override
    public int hashCode() { return Objects.hash(claim, citation, evidenceType); }
}
