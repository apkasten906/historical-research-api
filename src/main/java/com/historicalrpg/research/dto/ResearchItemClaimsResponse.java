package com.historicalrpg.research.dto;

import com.historicalrpg.research.entity.Citation;
import com.historicalrpg.research.entity.Claim;
import com.historicalrpg.research.entity.ClaimEvidence;
import com.historicalrpg.research.entity.ClaimType;
import com.historicalrpg.research.entity.EntityType;
import com.historicalrpg.research.entity.EvidenceStatus;
import com.historicalrpg.research.entity.EvidenceType;
import com.historicalrpg.research.entity.KnowledgeEntity;
import com.historicalrpg.research.entity.ResearchItem;
import com.historicalrpg.research.entity.Source;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Detached provenance view; constructed entirely within the service transaction. */
public record ResearchItemClaimsResponse(String code, String title, List<ClaimResponse> claims) {
    public static ResearchItemClaimsResponse fromEntity(ResearchItem item) {
        return new ResearchItemClaimsResponse(item.getCode(), item.getTitle(),
                item.getClaims().stream().map(ClaimResponse::fromEntity).toList());
    }

    public record EntityReference(UUID publicId, String name, EntityType type) {
        static EntityReference fromEntity(KnowledgeEntity entity) {
            return entity == null ? null : new EntityReference(
                    entity.getPublicId(), entity.getCanonicalName(), entity.getEntityType());
        }
    }

    public record SourceResponse(UUID publicId, String sourceType, String title, String author,
            Integer publicationYear, String publisher, String url, String bibliographicNote, String notes) {
        static SourceResponse fromEntity(Source source) {
            return new SourceResponse(source.getPublicId(), source.getSourceType(), source.getTitle(),
                    source.getAuthor(), source.getPublicationYear(), source.getPublisher(), source.getUrl(),
                    source.getBibliographicNote(), source.getNotes());
        }
    }

    public record CitationResponse(UUID publicId, String locator, Integer pageStart, Integer pageEnd,
            String quotation, String quotationLanguage, String notes, SourceResponse source) {
        static CitationResponse fromEntity(Citation citation) {
            return new CitationResponse(citation.getPublicId(), citation.getLocator(), citation.getPageStart(),
                    citation.getPageEnd(), citation.getQuotation(), citation.getQuotationLanguage(),
                    citation.getNotes(), SourceResponse.fromEntity(citation.getSource()));
        }
    }

    public record EvidenceResponse(EvidenceType evidenceType, String strength, String notes,
            CitationResponse citation) {
        static EvidenceResponse fromEntity(ClaimEvidence evidence) {
            return new EvidenceResponse(evidence.getEvidenceType(), evidence.getStrength(), evidence.getNotes(),
                    CitationResponse.fromEntity(evidence.getCitation()));
        }
    }

    public record ClaimResponse(UUID publicId, EntityReference subject, String predicate,
            EntityReference object, String literalValue, ClaimType claimType, EvidenceStatus evidenceStatus,
            LocalDate validFrom, LocalDate validTo, String datePrecision, String dateNote, String notes,
            List<EvidenceResponse> evidence) {
        static ClaimResponse fromEntity(Claim claim) {
            return new ClaimResponse(claim.getPublicId(), EntityReference.fromEntity(claim.getSubject()),
                    claim.getPredicate(), EntityReference.fromEntity(claim.getObject()), claim.getLiteralValue(),
                    claim.getClaimType(), claim.getEvidenceStatus(), claim.getValidFrom(), claim.getValidTo(),
                    claim.getDatePrecision(), claim.getDateNote(), claim.getNotes(),
                    claim.getEvidence().stream().map(EvidenceResponse::fromEntity).toList());
        }
    }
}
