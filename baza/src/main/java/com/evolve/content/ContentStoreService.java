package com.evolve.content;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLConnection;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentStoreService {
    private final FileRepository fileRepository;
    private final FileContentStore fileContentStore;

    public ContentFile setContent(File file) throws IOException {
        final String contentId = UUID.randomUUID().toString();
        final String mimeType = URLConnection.guessContentTypeFromName(file.getName());

        final ContentFile contentFile = ContentFile.builder()
                .name(file.getName())
                .contentId(contentId)
                .contentMimeType(mimeType)
                .contentLength(file.length())
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
}
