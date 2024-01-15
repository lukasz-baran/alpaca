package com.evolve.gui.documents;

import com.evolve.content.ContentFile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class DocumentEntry {
    private final Long id;
    private final String fileName;
    private final LocalDateTime dateAdded;
    private final String summary;
    private final String mimeType;
    private final Long length;

    static DocumentEntry of(ContentFile contentFile) {
        return new DocumentEntry(contentFile.getId(), contentFile.getName(), contentFile.getCreated(),
                contentFile.getSummary(), contentFile.getContentMimeType(), contentFile.getContentLength());
    }

}
