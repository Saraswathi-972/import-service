package com.workforce.importservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workforce.importservice.client.EmployeeServiceClient;
import com.workforce.importservice.dto.BalanceImportRecord;
import com.workforce.importservice.dto.EmployeeBalanceRequest;
import com.workforce.importservice.dto.ImportMessage;
import com.workforce.importservice.entity.BalanceImportRecordEntity;
import com.workforce.importservice.entity.ImportJob;
import com.workforce.importservice.entity.ImportRecordStatus;
import com.workforce.importservice.entity.ImportStatus;
import com.workforce.importservice.repository.BalanceImportRecordRepository;
import com.workforce.importservice.repository.ImportJobRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImportProcessingServiceImpl implements ImportProcessingService {
    private final FileStorageService fileStorageService;
    private final CsvParserService csvParserService;
    private final BalanceImportValidator balanceImportValidator;
    private final ImportJobRepository importJobRepository;
    private final BalanceImportRecordRepository balanceImportRecordRepository;
    private final EmployeeServiceClient employeeServiceClient;
    private final ObjectMapper objectMapper;

    public ImportProcessingServiceImpl(FileStorageService fileStorageService, CsvParserService csvParserService, BalanceImportValidator balanceImportValidator, ImportJobRepository importJobRepository, BalanceImportRecordRepository balanceImportRecordRepository, EmployeeServiceClient employeeServiceClient, ObjectMapper objectMapper) {
        this.fileStorageService = fileStorageService;
        this.csvParserService = csvParserService;
        this.balanceImportValidator = balanceImportValidator;
        this.importJobRepository = importJobRepository;
        this.balanceImportRecordRepository = balanceImportRecordRepository;
        this.employeeServiceClient=employeeServiceClient;
        this.objectMapper=objectMapper;
    }

    @Override
    public void processImport(ImportMessage importMessage) {
        System.out.println("Processing import message: " + importMessage);
        ImportJob importJob = importJobRepository.findById(importMessage.getImportId())
                .orElseThrow(() -> new RuntimeException("Import job not found with ID: " + importMessage.getImportId()));

        if (importJob.getStatus() == ImportStatus.COMPLETED) {
            System.out.println(
                    "Import job " + importJob.getId()
                            + " is already completed. Skipping duplicate message."
            );
            return;
        }

        importJob.setStatus(ImportStatus.PROCESSING);
        importJobRepository.save(importJob);
        try (InputStream inputStream = fileStorageService.downloadFile(importMessage.getS3Key())) {
            System.out.println("File downloaded successfully from S3 with key: " + importMessage.getS3Key());
            int successfulRecords = 0;
            int failedRecords = 0;
            List<BalanceImportRecord> records = csvParserService.parseCsv(inputStream);
            for (BalanceImportRecord record : records) {
                String validationError = balanceImportValidator.validateBalanceRecord(record);
                if (validationError != null) {

                    System.out.println("Validation error for record with AAID " + record.getAaid() + ": " + validationError);

                    BalanceImportRecordEntity entity = buildRecordEntity(record, importJob, ImportRecordStatus.FAILED, validationError);
                    balanceImportRecordRepository.save(entity);
                    failedRecords++;
                    continue;
                }
                EmployeeBalanceRequest request = EmployeeBalanceRequest.builder()
                        .employeeNumber(record.getAaid())
                        .balanceName(record.getBalanceName())
                        .balanceValue(record.getBalanceValue())
                        .action(record.getAction())
                        .build();
                try {
                    employeeServiceClient.processBalance(request);

                    BalanceImportRecordEntity entity = buildRecordEntity(record, importJob, ImportRecordStatus.SUCCESS, null);
                    balanceImportRecordRepository.save(entity);
                    successfulRecords++;
                }
                catch (FeignException e) {

                    String errorMessage;
                    System.out.println("Feign status: " + e.status());
                    System.out.println("Feign response body: " + e.contentUTF8());

                    try {
                        JsonNode root = objectMapper.readTree(e.contentUTF8());

                        if (root.has("message")) {
                            errorMessage = root.get("message").asText();
                        } else {
                            errorMessage = "Employee service returned an error";
                        }

                    } catch (Exception parseException) {

                        System.out.println(
                                "Could not parse employee-service error response: "
                                        + parseException.getMessage()
                        );

                        errorMessage = "Employee service returned an error";
                    }

                    System.out.println(
                            "Error processing balance for AAID "
                                    + record.getAaid()
                                    + ": "
                                    + errorMessage
                    );

                    BalanceImportRecordEntity entity =
                            buildRecordEntity(
                                    record,
                                    importJob,
                                    ImportRecordStatus.FAILED,
                                    errorMessage
                            );

                    balanceImportRecordRepository.save(entity);
                    failedRecords++;
                }

                System.out.println("AAID: " + record.getAaid() +
                        ", BalanceName: " + record.getBalanceName()
                        + ", BalanceValue: " + record.getBalanceValue()
                        + ", Action: " + record.getAction() + ", EffectiveDate: "
                        + record.getEffectiveDate() + ", Comments: "
                        + record.getComments());


            }

            importJob.setTotalRecords(records.size());
            importJob.setSuccessfulRecords(successfulRecords);
            importJob.setFailedRecords(failedRecords);
            importJob.setStatus(ImportStatus.COMPLETED);
            importJob.setCompletedAt(LocalDateTime.now());

            importJobRepository.save(importJob);
        } catch (Exception e) {
            importJob.setStatus(ImportStatus.FAILED);
            importJob.setCompletedAt(LocalDateTime.now());
            importJobRepository.save(importJob);

            throw new RuntimeException("Failed to process import", e);
        }
    }
    private BalanceImportRecordEntity buildRecordEntity(
            BalanceImportRecord record,
            ImportJob importJob,
            ImportRecordStatus status,
            String errorMessage) {

        return BalanceImportRecordEntity.builder()
                .importJob(importJob)
                .aaid(record.getAaid())
                .balanceName(record.getBalanceName())
                .balanceValue(record.getBalanceValue())
                .action(record.getAction())
                .effectiveDate(record.getEffectiveDate())
                .comments(record.getComments())
                .importRecordStatus(status)
                .errorMessage(errorMessage)
                .build();
    }
}
