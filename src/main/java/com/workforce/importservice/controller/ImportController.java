package com.workforce.importservice.controller;

import com.workforce.importservice.dto.ImportResponse;
import com.workforce.importservice.service.ImportService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/imports")
public class ImportController {
    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ImportResponse createImport(@RequestParam("file") MultipartFile file) {
        return importService.createImport(file);
    }

}
