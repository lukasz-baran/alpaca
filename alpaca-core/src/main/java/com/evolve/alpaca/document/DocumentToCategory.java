package com.evolve.alpaca.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class DocumentToCategory {

    @Id
    private Long contentId;

    private DocumentCategory documentCategory;


}
