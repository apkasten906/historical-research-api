package com.historicalrpg.research.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "citation")
public class Citation extends KnowledgeRecord {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @Column(length = 255)
    private String locator;

    private Integer pageStart;

    private Integer pageEnd;

    @Column(columnDefinition = "text")
    private String quotation;

    @Column(length = 20)
    private String quotationLanguage;

    @Column(columnDefinition = "text")
    private String notes;

    public Source getSource() { return source; }
    public void setSource(Source source) { this.source = source; }

    public String getLocator() { return locator; }
    public void setLocator(String locator) { this.locator = locator; }

    public Integer getPageStart() { return pageStart; }
    public void setPageStart(Integer pageStart) { this.pageStart = pageStart; }

    public Integer getPageEnd() { return pageEnd; }
    public void setPageEnd(Integer pageEnd) { this.pageEnd = pageEnd; }

    public String getQuotation() { return quotation; }
    public void setQuotation(String quotation) { this.quotation = quotation; }

    public String getQuotationLanguage() { return quotationLanguage; }
    public void setQuotationLanguage(String quotationLanguage) { this.quotationLanguage = quotationLanguage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
