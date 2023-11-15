package com.evolve.content;

import org.springframework.content.commons.repository.ContentStore;
import org.springframework.stereotype.Repository;

@Repository
public interface FileContentStore extends ContentStore<ContentFile, String> {
}