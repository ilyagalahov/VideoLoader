package com.empiritek.maground;


import com.empiritek.maground.domain.Index;
import com.empiritek.maground.repository.IndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Component
public class ScheduledTasks {

    @Autowired
    private IndexRepository repository;

    @Scheduled(fixedRate = 5000)
    public void scanFolder() {
        System.out.println("Scanning folder");
        try (Stream<Path> stream = Files.list(Paths.get("/maground-archive"))) {
            stream.forEach(new Creator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Creator implements Consumer<Path> {

        @Override
        public void accept(Path path) {
            try {
                System.out.println("Saving file : " + path.toString());
                repository.save(new Index(getMd5(path), path.getFileName().toString()));
                Files.delete(path);

                System.out.println("Files in database : " + repository.count());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String getMd5(Path path) throws IOException {
            return DigestUtils.md5DigestAsHex(Files.newInputStream(path));
        }
    }
}