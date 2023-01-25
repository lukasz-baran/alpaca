package com.evolve.content;

import org.springframework.content.commons.repository.ContentStore;

import java.io.File;

public interface FileContentStore extends ContentStore<File, String> {
}