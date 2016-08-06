package com.empiritek.maground.repository;

import com.empiritek.maground.domain.FolderMetadata;
import org.springframework.data.repository.CrudRepository;


public interface FolderMetadataRepository extends CrudRepository<FolderMetadata, String> {
    FolderMetadata findByFolderName(String folderName);
}
