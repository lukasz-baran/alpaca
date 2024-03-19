package com.evolve.alpaca.document.services;

import com.evolve.alpaca.document.*;
import com.evolve.alpaca.document.repo.DocumentToCategoryRepository;
import com.evolve.content.ContentFile;
import com.evolve.content.ContentStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentContentStorageService {

    private final ContentStoreService contentStoreService;
    private final DocumentToCategoryRepository documentToCategoryRepository;

    public List<DocumentEntry> findPersonsDocuments(String personId) {
        final List<ContentFile> contentFiles = contentStoreService.findFiles(personId);

        final List<DocumentToCategory> categories = documentToCategoryRepository.findAllById(
                contentFiles.stream().map(ContentFile::getId).toList());

        final Map<Long, DocumentCategory> idToCategories = categories.stream()
                .filter(category -> Objects.nonNull(category.getDocumentCategory()))
                .collect(Collectors.toMap(DocumentToCategory::getContentId, DocumentToCategory::getDocumentCategory));

        return contentFiles.stream()
                .map(contentFile -> DocumentEntry.of(contentFile, idToCategories.get(contentFile.getId())))
                .toList();
    }

    public DocumentEntry storeContent(String personId, FilePathAndDescription filePathAndDescription) {
        final File file = filePathAndDescription.getFile();
        final String description = filePathAndDescription.getDescription();
        final DocumentCategory targetCategory = filePathAndDescription.getDocumentCategory();

        final ContentFile contentFile = contentStoreService.setContent(file, personId, description);
        log.info("Content file added: {}", contentFile);

        documentToCategoryRepository.save(new DocumentToCategory(contentFile.getId(), targetCategory));

        return DocumentEntry.of(contentFile, targetCategory);
    }

    public void updateContent(UpdateDocumentCommand command) {
        contentStoreService.changeFileDetails(command.id(), command.fileName(), command.summary());

        if (command.category() == null || command.category() == DocumentCategory.DEFAULT) {
            documentToCategoryRepository.findById(command.id())
                            .ifPresent(documentToCategoryRepository::delete);

        } else {
            documentToCategoryRepository.save(new DocumentToCategory(command.id(), command.category()));
        }
    }

    public void removeContent(Long id) {
        contentStoreService.deleteContent(id);
        documentToCategoryRepository.findById(id)
                .ifPresent(documentToCategoryRepository::delete);
    }

    public File saveToTempFile(DocumentEntry documentEntry) throws IOException {
        final InputStream inputStream = contentStoreService.getContent(documentEntry.getId());

        final String prefix = FilenameUtils.getBaseName(documentEntry.getFileName());
        final String suffix = "." + FilenameUtils.getExtension(documentEntry.getFileName());
        final File tempFile = File.createTempFile(prefix, suffix);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(inputStream, out);
        }
        return tempFile;
    }

    public void saveToFile(Long imageFileId, File targetFile) throws IOException {
        final InputStream inputStream = contentStoreService.getContent(imageFileId);
        log.info("saving to file: {}", targetFile);
        FileUtils.copyInputStreamToFile(inputStream, targetFile);
    }

}
