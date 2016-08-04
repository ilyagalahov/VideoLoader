package com.empiritek.maground.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
public class FileDataController {

    @RequestMapping(value = "/{fileId}")
    public String getById(@PathVariable String fileId) {
        return "You have requested file with id " + fileId;
    }
}
