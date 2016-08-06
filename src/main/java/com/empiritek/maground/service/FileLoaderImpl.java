package com.empiritek.maground.service;

import com.empiritek.maground.domain.FileMetadata;
import com.empiritek.maground.domain.FolderMetadata;
import com.empiritek.maground.repository.FileMetadataRepository;
import com.empiritek.maground.repository.FolderMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashMap;


@Service
@SuppressWarnings({"SpringJavaAutowiringInspection", "SpringAutowiredFieldsWarningInspection"})
public class FileLoaderImpl implements FileLoader {

    @Autowired
    FolderMetadataRepository folderMetadataRepository;
    @Autowired
    FileMetadataRepository fileMetadataRepository;

    @Value("${files.rootFolder}")
    String ROOT_FOLDER;
    @Value("${files.storePath}")
    String STORE_PATH;

    @Override
    public void loadFoldersHierarchy(String path) throws IOException {

        Files.walkFileTree(Paths.get(path),
                new SimpleFileVisitor<Path>() {
                    HashMap<String, Long> pathWithDbId = new HashMap<>();

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        if (file.getFileName().toString().equals(".DS_Store")) {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        String md5 = getMd5(file);
                        createFileMetadata(file, md5);
                        copyToMd5Storage(file, md5);
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }


                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        if (dir.getFileName().toString().equals(ROOT_FOLDER)) {
                            FolderMetadata folderMetadata = createRottMetadataIfNecessary(dir);
                            pathWithDbId.put(dir.toAbsolutePath().toString(), folderMetadata.getId());
                            return FileVisitResult.CONTINUE;
                        } else {
                            FolderMetadata folderMetadata = createFolderMetadata(dir);
                            pathWithDbId.put(dir.toAbsolutePath().toString(), folderMetadata.getId());

                            return FileVisitResult.CONTINUE;
                        }
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                        if (dir.getFileName().toString().equals(ROOT_FOLDER))
                            return FileVisitResult.TERMINATE;
                        if (e == null) {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            throw e;
                        }
                    }

                    private FolderMetadata createFolderMetadata(Path dir) {
                        FolderMetadata folderMetadata = new FolderMetadata();
                        folderMetadata.setDate(new Date());
                        folderMetadata.setFolderName(dir.getFileName().toString());
                        if (pathWithDbId.get(dir.getParent().toAbsolutePath().toString()) != null)
                            folderMetadata.setParentId(pathWithDbId.get(dir.getParent().toAbsolutePath().toString()));
                        folderMetadataRepository.save(folderMetadata);
                        return folderMetadata;
                    }

                    private void createFileMetadata(Path file, String md5) {
                        FileMetadata fileMetadata = new FileMetadata();
                        fileMetadata.setMd5(md5);
                        fileMetadata.setInitialFileName(file.getFileName().toString());
                        fileMetadata.setFolderId(pathWithDbId.get(file.getParent().toAbsolutePath().toString()));
                        fileMetadataRepository.save(fileMetadata);
                    }

                }
        );
    }

    private FolderMetadata createRottMetadataIfNecessary(Path dir) {
        FolderMetadata folderMetadata = folderMetadataRepository.findByFolderName(ROOT_FOLDER);
        if (folderMetadata == null) {
            folderMetadata = new FolderMetadata();
            folderMetadata.setDate(new Date());
            folderMetadata.setFolderName(dir.getFileName().toString());
            folderMetadataRepository.save(folderMetadata);
        }
        return folderMetadata;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void copyToMd5Storage(Path file, String md5) throws IOException {
        StringBuilder copyPath = new StringBuilder();
        copyPath.append(STORE_PATH)
                .append("/").append(md5.charAt(0))
                .append("/").append(md5.charAt(1));

        File copyDir = new File(copyPath.toString());
        copyDir.mkdirs();

        //TODO: remove hardcode
        Path pathToCopy = Paths.get(copyPath.append("/").append(md5).append(".mp4").toString());
        Files.copy(file, pathToCopy);
    }

    private String getMd5(Path path) throws IOException {
        return DigestUtils.md5DigestAsHex(Files.newInputStream(path));
    }

}
