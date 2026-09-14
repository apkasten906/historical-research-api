package com.historicalrpg.research.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "source")
public class Source extends KnowledgeRecord {

    @Column(nullable = false, length = 50)
    private String sourceType;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 500)
    private String author;

    private Integer publicationYear;

    @Column(length = 500)
    private String publisher;

    @Column(columnDefinition = "text")
    private String url;

    @Column(columnDefinition = "text")
    private String bibliographicNote;

    @Column(columnDefinition = "text")
    private String notes;

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getBibliographicNote() { return bibliographicNote; }
    public void setBibliographicNote(String bibliographicNote) { this.bibliographicNote = bibliographicNote; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
