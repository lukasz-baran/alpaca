package com.evolve.alpaca.document;

import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentEntryAssertion extends AbstractAssert<DocumentEntryAssertion,DocumentEntry> {
    public DocumentEntryAssertion(DocumentEntry documentEntry) {
        super(documentEntry, DocumentEntryAssertion.class);
    }

    public DocumentEntryAssertion hasFileName(String fileName) {
        assertThat(actual.getFileName()).isEqualTo(fileName);
        return this;
    }

    public DocumentEntryAssertion hasSummary(String summary) {
        assertThat(actual.getSummary()).isEqualTo(summary);
        return this;
    }

    public DocumentEntryAssertion hasMimeType(String mimeType) {
        assertThat(actual.getMimeType()).isEqualTo(mimeType);
        return this;
    }

    public DocumentEntryAssertion hasLength(Long length) {
        assertThat(actual.getLength()).isEqualTo(length);
        return this;
    }

    public DocumentEntryAssertion isDocument() {
        assertThat(actual.getEntryType()).isEqualTo(DocumentEntry.EntryType.DOCUMENT);
        return this;
    }

    public DocumentEntryAssertion hasNoCategory() {
        assertThat(actual.getCategory()).isNull();
        return this;
    }

    public DocumentEntryAssertion hasId(Long id) {
        assertThat(actual.getId()).isEqualTo(id);
        return this;
    }

    public DocumentEntryAssertion hasCategory(DocumentCategory documentCategory) {
        assertThat(actual.getCategory()).isEqualTo(documentCategory);
        return this;
    }
}
