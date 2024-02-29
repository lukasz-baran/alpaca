package com.evolve.alpaca.document;

import com.evolve.content.ContentFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;

/**
 * POJO that contains information about document's content and category.
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class DocumentEntry {
    private final Long id;
    @Setter
    private String fileName;
    private final LocalDateTime dateAdded;
    @Setter
    private String summary;
    private final String mimeType;
    private final Long length;

    private EntryType entryType;
    @Setter
    private DocumentCategory category;

    @JsonIgnore
    public boolean isImageFile() {
        return StringUtils.contains(mimeType, "image");
    }

    public boolean matchesCategory(DocumentCategory documentCategory) {
        if (documentCategory == DocumentCategory.DEFAULT && this.category == null) {
            return true;
        }

        return this.category == documentCategory;
    }

    public static DocumentEntry of(ContentFile contentFile, DocumentCategory documentCategory) {
        return new DocumentEntry(contentFile.getId(), contentFile.getName(), contentFile.getCreated(),
                contentFile.getSummary(), contentFile.getContentMimeType(), contentFile.getContentLength(),
                EntryType.DOCUMENT, documentCategory);
    }


    public static DocumentEntry category(DocumentCategory category) {
        return new DocumentEntry(null, null, null, null, null, null, DocumentEntry.EntryType.CATEGORY, category);
    }

    public enum EntryType {
        CATEGORY,
        DOCUMENT
    }

}
