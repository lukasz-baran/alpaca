package com.evolve.content;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLConnection;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentStoreService {
    private final FileRepository fileRepository;
    private final FileContentStore fileContentStore;

    public ContentFile setContent(File file) throws IOException {
        final String contentId = UUID.randomUUID().toString();
        final String mimeType = URLConnection.guessContentTypeFromName(file.getName());

        final ContentFile f = ContentFile.builder()
                .name(file.getName())
                .contentId(contentId)
                .contentMimeType(mimeType)
                .contentLength(file.length())
                .build();
        System.out.println(contentId);

        return fileRepository.save(fileContentStore.setContent(f, new FileInputStream(file)));
    }

    @SneakyThrows
    public InputStream getContent(Long id) {
        ContentFile contentFile = fileRepository.getById(id);
        System.out.println(contentFile);
        return fileContentStore.getContent(contentFile);
    }
}
