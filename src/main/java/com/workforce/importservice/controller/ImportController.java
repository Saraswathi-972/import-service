package com.workforce.importservice.controller;

import com.workforce.importservice.dto.ImportRecordsPageResponse;
import com.workforce.importservice.dto.ImportResponse;
import com.workforce.importservice.dto.ImportResultResponse;
import com.workforce.importservice.entity.ImportStatus;
import com.workforce.importservice.service.ImportResultService;
import com.workforce.importservice.service.ImportService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/imports")
public class ImportController {
    private final ImportService importService;
    private final ImportResultService importResultService;

    public ImportController(ImportService importService, ImportResultService importResultService) {
        this.importService = importService;
        this.importResultService = importResultService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ImportResponse createImport(@RequestParam("file") MultipartFile file) {
        return importService.createImport(file);
    }

    @GetMapping("/{importId}")
    public ImportResultResponse getImportResults(@PathVariable Long importId) {
        return importResultService.getImportResults(importId);
    }

    @GetMapping("/{importId}/records")
    public ImportRecordsPageResponse getImportRecords(
            @PathVariable Long importId,
            Pageable pageable) {

        return importResultService.getImportRecords(importId, pageable);
    }

}
