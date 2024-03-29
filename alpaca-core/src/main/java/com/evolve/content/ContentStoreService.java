package com.evolve.content;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class ContentStoreService {
    private final FileRepository fileRepository;
    private final FileContentStore fileContentStore;
    private final Clock clock;

    @SneakyThrows
    public ContentFile setContent(File file, String personId, String summary) {
        final String contentId = UUID.randomUUID().toString();
        final String mimeType = URLConnection.guessContentTypeFromName(file.getName());

        final ContentFile contentFile = ContentFile.builder()
                .name(file.getName())
                .contentId(contentId)
                .contentMimeType(mimeType)
                .contentLength(file.length())
                .personId(personId)
                .created(getCurrentTime())
                .summary(summary)
                .build();
        log.info("saving file {}", contentFile);

        return fileRepository.save(fileContentStore.setContent(contentFile, new FileInputStream(file)));
    }

    @SneakyThrows
    public InputStream getContent(Long id) {
        ContentFile contentFile = fileRepository.getById(id);
        log.info("reading file {}", contentFile);
        return fileContentStore.getContent(contentFile);
    }

    public List<ContentFile> findFiles(String personId) {
        return fileRepository.findByPersonId(personId);
    }

    public void deleteContent(Long id) {
        log.info("deleting file {}", id);
        fileContentStore.unsetContent(fileRepository.getById(id));
        fileRepository.deleteById(id);
    }

    public void changeFileDetails(Long id, String newFileName, String newSummary) {
        final ContentFile contentFile = fileRepository.getById(id);
        contentFile.setName(newFileName);
        contentFile.setSummary(newSummary);
        fileRepository.save(contentFile);
    }

    /**
     * Truncate nanoseconds because H2 by default only offers milliseconds precision
     */
    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.MILLIS);
    }
}
