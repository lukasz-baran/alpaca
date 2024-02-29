package com.evolve.alpaca.document;

import com.evolve.AlpacaAbstractIntegrationTest;
import com.evolve.alpaca.document.services.DocumentContentStorageService;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class DocumentStoringTest extends AlpacaAbstractIntegrationTest {

    public static final String PERSON_ID = "02001";
    public static final String FILENAME = "1x1-ffff007f.png";
    public static final URL CONTENT_FILE = Resources.getResource("files/" + FILENAME);
    public static final String DESCRIPTION = "smallest PNG file";

    @Autowired
    DocumentContentStorageService documentContentStorageService;

    @Test
    void storeFileWithoutCategory() {
        // given
        final File file = new File(CONTENT_FILE.getFile());

        final FilePathAndDescription filePathAndDescription = new FilePathAndDescription();
        filePathAndDescription.setFile(file);
        filePathAndDescription.setDescription(DESCRIPTION);

        // when
        final DocumentEntry result = documentContentStorageService.storeContent(PERSON_ID, filePathAndDescription);
        final List<DocumentEntry> documentEntries = documentContentStorageService.findPersonsDocuments(PERSON_ID);

        // then
        assertThat(documentEntries, DocumentEntryAssertion.class)
                .hasSize(1)
                .first()
                .isDocument()
                .hasId(result.getId())
                .hasFileName(FILENAME)
                .hasLength(70L)
                .hasNoCategory()
                .hasMimeType("image/png")
                .hasSummary(DESCRIPTION);
    }

    @Test
    void storeFileWithCategory() {
        // given
        final File file = new File(CONTENT_FILE.getFile());
        final FilePathAndDescription filePathAndDescription = new FilePathAndDescription(file, DESCRIPTION, DocumentCategory.FORM);

        // when
        final DocumentEntry result = documentContentStorageService.storeContent(PERSON_ID, filePathAndDescription);

        // then
        assertThat(documentContentStorageService.findPersonsDocuments(PERSON_ID), DocumentEntryAssertion.class)
                .hasSize(1)
                .first()
                .isDocument()
                .hasId(result.getId())
                .hasFileName(FILENAME)
                .hasLength(70L)
                .hasCategory(DocumentCategory.FORM)
                .hasMimeType("image/png")
                .hasSummary(DESCRIPTION);

        // when -- change category
        documentContentStorageService.updateContent(new UpdateDocumentCommand(
                result.getId(), result.getFileName(), result.getSummary(), DocumentCategory.CORRESPONDENCE));

        // then
        assertThat(documentContentStorageService.findPersonsDocuments(PERSON_ID), DocumentEntryAssertion.class)
                .hasSize(1)
                .first()
                .hasCategory(DocumentCategory.CORRESPONDENCE);

        // when -- change category to none
        documentContentStorageService.updateContent(new UpdateDocumentCommand(
                result.getId(), result.getFileName(), result.getSummary(), DocumentCategory.DEFAULT));

        // then
        assertThat(documentContentStorageService.findPersonsDocuments(PERSON_ID), DocumentEntryAssertion.class)
                .hasSize(1)
                .first()
                .hasNoCategory();

        // when -- edit summary
        documentContentStorageService.updateContent(new UpdateDocumentCommand(
                result.getId(), result.getFileName(), "changing only summary", DocumentCategory.DEFAULT));

        // then
        assertThat(documentContentStorageService.findPersonsDocuments(PERSON_ID), DocumentEntryAssertion.class)
                .hasSize(1)
                .first()
                .hasSummary("changing only summary");
    }


}
