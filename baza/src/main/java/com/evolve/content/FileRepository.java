package com.evolve.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<ContentFile, Long> {

    List<ContentFile> findByPersonId(String personId);

}
