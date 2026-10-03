package com.workforce.importservice.controller;

import com.workforce.importservice.dto.FailedRecordResponse;
import com.workforce.importservice.dto.ImportRecordsPageResponse;
import com.workforce.importservice.dto.ImportResponse;
import com.workforce.importservice.dto.ImportResultResponse;
import com.workforce.importservice.entity.ImportStatus;
import com.workforce.importservice.service.EmailNotificationService;
import com.workforce.importservice.service.ImportResultService;
import com.workforce.importservice.service.ImportService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/imports")
public class ImportController {
    private final ImportService importService;
    private final ImportResultService importResultService;
    private final EmailNotificationService emailNotificationService;

    public ImportController(ImportService importService, ImportResultService importResultService, EmailNotificationService emailNotificationService) {
        this.importService = importService;
        this.importResultService = importResultService;
        this.emailNotificationService = emailNotificationService;
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

    @GetMapping("/test-email")
    public String testEmail() {

        ImportResultResponse result = ImportResultResponse.builder()
                .importId(999L)
                .fileName("test-balance.csv")
                .status(ImportStatus.COMPLETED)
                .totalRecords(5)
                .successfulRecords(3)
                .failedRecords(2)
                .completedAt(LocalDateTime.now())
                .failedRecordDetails(List.of(
                        FailedRecordResponse.builder()
                                .aaid("949499")
                                .balanceName("SICK HOURS")
                                .action("SET")
                                .balanceValue(new BigDecimal("10"))
                                .effectiveDate(LocalDate.of(2026, 8, 20))
                                .comments("")
                                .status("FAILED")
                                .errorMessage(
                                        "Employee not found with employeeNumber: 949499"
                                )
                                .build()
                ))
                .build();

        emailNotificationService.sendImportResultEmail(result);

        return "Test email sent";
    }

}
