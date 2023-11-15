package com.evolve.content;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<ContentFile, Long> {

    List<ContentFile> findByPersonId(String personId);

}
