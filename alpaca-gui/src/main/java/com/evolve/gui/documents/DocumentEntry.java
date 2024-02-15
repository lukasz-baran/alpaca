package com.evolve.gui.documents;

import com.evolve.content.ContentFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;

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

    @JsonIgnore
    public boolean isImageFile() {
        return StringUtils.contains(mimeType, "image");
    }

    static DocumentEntry of(ContentFile contentFile) {
        return new DocumentEntry(contentFile.getId(), contentFile.getName(), contentFile.getCreated(),
                contentFile.getSummary(), contentFile.getContentMimeType(), contentFile.getContentLength());
    }

}
