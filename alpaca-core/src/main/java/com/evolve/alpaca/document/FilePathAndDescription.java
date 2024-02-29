package com.evolve.alpaca.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilePathAndDescription {
    private File file;
    private String description;
    private DocumentCategory documentCategory;

}
