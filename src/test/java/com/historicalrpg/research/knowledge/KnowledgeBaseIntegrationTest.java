package com.historicalrpg.research.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.historicalrpg.research.entity.*;
import com.historicalrpg.research.repository.*;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(properties = "spring.jpa.open-in-view=false")
@AutoConfigureMockMvc
class KnowledgeBaseIntegrationTest {
    @Autowired private EntityManager em;
    @Autowired private PlatformTransactionManager transactionManager;
    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private ClaimRepository claims;
    @Autowired private ResearchItemRepository items;
    @Autowired private KnowledgeEntityRepository entities;
    @Autowired private SourceRepository sources;
    @Autowired private CitationRepository citations;
    @Autowired private ClaimEvidenceRepository evidence;
    @Autowired private EntityAliasRepository aliases;

    @Test
    void persistsAllClaimKindsAndReturnsConflictingProvenanceOutsideTheWriteTransaction() throws Exception {
        String code = "TEST-" + UUID.randomUUID();
        String otherCode = "TEST-" + UUID.randomUUID();
        Fixture fixture = new TransactionTemplate(transactionManager).execute(tx -> {
            KnowledgeEntity subject = entity("Synthetic envoy", EntityType.PERSON);
            KnowledgeEntity object = entity("Synthetic city", EntityType.PLACE);
            EntityAlias alias = new EntityAlias();
            alias.setEntity(subject);
            alias.setAlias("Synthetic alternate name");
            alias.setLanguage("en");
            alias.setAliasType("ALTERNATE");
            aliases.saveAndFlush(alias);

            Source source = new Source();
            source.setSourceType("TEST_TRANSCRIPT");
            source.setTitle("Synthetic source — not historical evidence");
            source.setAuthor("Test fixture");
            sources.saveAndFlush(source);
            Citation supporting = citation(source, "p. 1");
            Citation contradicting = citation(source, "p. 2");
            Claim fact = claim(subject, object, null, ClaimType.HISTORICAL_FACT, EvidenceStatus.DOCUMENTED);
            fact.setValidFrom(LocalDate.of(1488, 1, 1));
            fact.setValidTo(LocalDate.of(1488, 12, 31));
            fact.setDatePrecision("CIRCA");
            fact.setDateNote("Synthetic uncertain date window");
            claims.saveAndFlush(fact);
            addEvidence(fact, supporting, EvidenceType.SUPPORTS);
            addEvidence(fact, contradicting, EvidenceType.CONTRADICTS);
            // The same citation may also contextualize a claim.
            addEvidence(fact, supporting, EvidenceType.CONTEXT);
            Claim inference = claim(subject, null, "Synthetic possible motive",
                    ClaimType.HISTORICAL_INFERENCE, EvidenceStatus.PLAUSIBLE);
            Claim invention = claim(subject, null, "Synthetic fictional quest",
                    ClaimType.GAME_INVENTION, EvidenceStatus.FICTIONAL);
            ResearchItem item = item(code);
            item.getClaims().add(fact);
            item.getClaims().add(inference);
            item.getClaims().add(invention);
            ResearchItem other = item(otherCode);
            other.getClaims().add(fact);
            em.flush();
            em.clear();
            assertThat(claims.findById(fact.getId()).orElseThrow().getEvidence()).hasSize(3);
            assertThat(aliases.findById(alias.getId()).orElseThrow().getEntity().getPublicId())
                    .isEqualTo(subject.getPublicId());
            assertThat(claims.findById(inference.getId()).orElseThrow().getEvidenceStatus())
                    .isEqualTo(EvidenceStatus.PLAUSIBLE);
            assertThat(claims.findById(invention.getId()).orElseThrow().getClaimType())
                    .isEqualTo(ClaimType.GAME_INVENTION);
            return new Fixture(subject.getId(), object.getId(), source.getId(), fact.getPublicId(),
                    subject.getPublicId(), object.getPublicId());
        });
        try {
            // No test transaction is active here: DTO mapping must finish in the service.
            mvc.perform(get("/api/research-items/{code}/claims", code))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(code))
                    .andExpect(jsonPath("$.title").value("Synthetic research item"))
                    .andExpect(jsonPath("$.claims.length()").value(3))
                    .andExpect(jsonPath("$.claims[0].publicId").value(fixture.claimPublicId().toString()))
                    .andExpect(jsonPath("$.claims[0].subject.publicId").value(fixture.subjectPublicId().toString()))
                    .andExpect(jsonPath("$.claims[0].subject.name").value("Synthetic envoy"))
                    .andExpect(jsonPath("$.claims[0].subject.type").value("PERSON"))
                    .andExpect(jsonPath("$.claims[0].object.publicId").value(fixture.objectPublicId().toString()))
                    .andExpect(jsonPath("$.claims[0].object.type").value("PLACE"))
                    .andExpect(jsonPath("$.claims[0].literalValue").isEmpty())
                    .andExpect(jsonPath("$.claims[0].predicate").value("synthetic_test_assertion"))
                    .andExpect(jsonPath("$.claims[0].claimType").value("HISTORICAL_FACT"))
                    .andExpect(jsonPath("$.claims[0].evidenceStatus").value("DOCUMENTED"))
                    .andExpect(jsonPath("$.claims[0].validFrom").value("1488-01-01"))
                    .andExpect(jsonPath("$.claims[0].validTo").value("1488-12-31"))
                    .andExpect(jsonPath("$.claims[0].datePrecision").value("CIRCA"))
                    .andExpect(jsonPath("$.claims[0].dateNote").value("Synthetic uncertain date window"))
                    .andExpect(jsonPath("$.claims[0].evidence[*].evidenceType")
                            .value(containsInAnyOrder("SUPPORTS", "CONTRADICTS", "CONTEXT")))
                    .andExpect(jsonPath("$.claims[0].evidence[0].citation.locator").value("p. 1"))
                    .andExpect(jsonPath("$.claims[0].evidence[0].citation.source.title")
                            .value("Synthetic source — not historical evidence"))
                    .andExpect(jsonPath("$.claims[1].literalValue").value("Synthetic possible motive"))
                    .andExpect(jsonPath("$.claims[1].object").isEmpty())
                    .andExpect(jsonPath("$.claims[1].claimType").value("HISTORICAL_INFERENCE"))
                    .andExpect(jsonPath("$.claims[1].evidenceStatus").value("PLAUSIBLE"))
                    .andExpect(jsonPath("$.claims[2].claimType").value("GAME_INVENTION"))
                    .andExpect(jsonPath("$.claims[2].evidenceStatus").value("FICTIONAL"));
            mvc.perform(get("/api/research-items/{code}/claims", otherCode))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.claims.length()").value(1))
                    .andExpect(jsonPath("$.claims[0].publicId").value(fixture.claimPublicId().toString()));
            new TransactionTemplate(transactionManager).executeWithoutResult(tx ->
                    items.delete(items.findByCode(otherCode).orElseThrow()));
            mvc.perform(get("/api/research-items/{code}/claims", code))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.claims.length()").value(3));
        } finally {
            new TransactionTemplate(transactionManager).executeWithoutResult(tx -> {
                items.findByCode(code).ifPresent(items::delete);
                items.findByCode(otherCode).ifPresent(items::delete);
                em.flush();
                jdbc.update("delete from claim where subject_entity_id = ?", fixture.subjectId());
                jdbc.update("delete from citation where source_id = ?", fixture.sourceId());
                sources.deleteById(fixture.sourceId());
                entities.deleteById(fixture.subjectId());
                entities.deleteById(fixture.objectId());
            });
        }
    }

    @Test
    void returnsEmptyClaimsAndNotFoundForUnknownItem() throws Exception {
        String code = "TEST-" + UUID.randomUUID();
        new TransactionTemplate(transactionManager).executeWithoutResult(tx -> item(code));
        try {
            mvc.perform(get("/api/research-items/{code}/claims", code))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.claims").isEmpty());
            mvc.perform(get("/api/research-items/{code}/claims", "MISSING-" + UUID.randomUUID()))
                    .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        } finally {
            new TransactionTemplate(transactionManager).executeWithoutResult(tx ->
                    items.delete(items.findByCode(code).orElseThrow()));
        }
    }

    @Test
    void rejectsClaimsWithBothOrNeitherObjectRepresentation() {
        for (boolean both : new boolean[] {false, true}) {
            assertThatThrownBy(() -> new TransactionTemplate(transactionManager).executeWithoutResult(tx -> {
                KnowledgeEntity subject = entity("Synthetic invalid claim subject", EntityType.PERSON);
                claim(subject, both ? subject : null, both ? "invalid second object" : null,
                        ClaimType.HISTORICAL_FACT, EvidenceStatus.DOCUMENTED);
            })).hasStackTraceContaining("ck_claim_object");
        }
    }

    @Test
    void legacyJpaInsertAndRenameMaintainEntityLink() {
        new TransactionTemplate(transactionManager).executeWithoutResult(tx -> {
            tx.setRollbackOnly();
            Location location = new Location();
            location.setName("Synthetic JPA place");
            em.persist(location);
            em.flush();
            em.refresh(location);
            Long entityId = location.getKnowledgeEntity().getId();
            assertThat(location.getKnowledgeEntity().getEntityType()).isEqualTo(EntityType.PLACE);
            location.setName("Synthetic renamed JPA place");
            em.flush();
            em.clear();
            assertThat(em.find(Location.class, location.getId()).getKnowledgeEntity().getId()).isEqualTo(entityId);
            assertThat(em.find(KnowledgeEntity.class, entityId).getCanonicalName()).isEqualTo("Synthetic renamed JPA place");
        });
    }

    private KnowledgeEntity entity(String name, EntityType type) {
        KnowledgeEntity entity = new KnowledgeEntity();
        entity.setCanonicalName(name);
        entity.setEntityType(type);
        return entities.saveAndFlush(entity);
    }

    private Citation citation(Source source, String locator) {
        Citation citation = new Citation();
        citation.setSource(source);
        citation.setLocator(locator);
        citation.setQuotation("Synthetic extract");
        citation.setQuotationLanguage("en");
        return citations.saveAndFlush(citation);
    }

    private Claim claim(KnowledgeEntity subject, KnowledgeEntity object, String literal,
            ClaimType type, EvidenceStatus status) {
        Claim claim = new Claim();
        claim.setSubject(subject);
        claim.setPredicate("synthetic_test_assertion");
        claim.setObject(object);
        claim.setLiteralValue(literal);
        claim.setClaimType(type);
        claim.setEvidenceStatus(status);
        return claims.saveAndFlush(claim);
    }

    private void addEvidence(Claim claim, Citation citation, EvidenceType type) {
        ClaimEvidence entry = new ClaimEvidence();
        entry.setClaim(claim);
        entry.setCitation(citation);
        entry.setEvidenceType(type);
        entry.setStrength("DIRECT");
        evidence.saveAndFlush(entry);
    }

    private ResearchItem item(String code) {
        ResearchItem item = new ResearchItem();
        item.setCode(code);
        item.setTitle("Synthetic research item");
        item.setStatus(ResearchStatus.OPEN);
        return items.saveAndFlush(item);
    }

    private record Fixture(Long subjectId, Long objectId, Long sourceId, UUID claimPublicId,
            UUID subjectPublicId, UUID objectPublicId) {}
}
