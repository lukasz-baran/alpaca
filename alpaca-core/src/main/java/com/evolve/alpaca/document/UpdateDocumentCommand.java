package com.evolve.alpaca.document;

public record UpdateDocumentCommand(Long id, String fileName, String summary, DocumentCategory category) {
}
