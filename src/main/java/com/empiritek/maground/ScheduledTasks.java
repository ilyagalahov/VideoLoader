package com.empiritek.maground;


import com.empiritek.maground.service.FileLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@Component
public class ScheduledTasks {

    @Autowired
    private FileLoader fileLoader;

    @Value("${files.inboxPath}")
    String INBOX_PATH;

    @Scheduled(fixedRate = 10000)
    public void scanFolder() {
        System.out.println("Scanning folder");

        try {
            fileLoader.loadFoldersHierarchy(INBOX_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}