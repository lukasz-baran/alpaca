package com.evolve.gui.documents;

import com.evolve.content.ContentFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;

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

    @JsonIgnore
    public boolean isImageFile() {
        return StringUtils.contains(mimeType, "image");
    }

    static DocumentEntry of(ContentFile contentFile) {
        return new DocumentEntry(contentFile.getId(), contentFile.getName(), contentFile.getCreated(),
                contentFile.getSummary(), contentFile.getContentMimeType(), contentFile.getContentLength());
    }

}
