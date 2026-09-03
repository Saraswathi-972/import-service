package com.workforce.importservice.service;

import com.workforce.importservice.dto.BalanceImportRecord;
import com.workforce.importservice.dto.ImportMessage;
import com.workforce.importservice.entity.BalanceImportRecordEntity;
import com.workforce.importservice.entity.ImportJob;
import com.workforce.importservice.entity.ImportRecordStatus;
import com.workforce.importservice.entity.ImportStatus;
import com.workforce.importservice.repository.BalanceImportRecordRepository;
import com.workforce.importservice.repository.ImportJobRepository;
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

    public ImportProcessingServiceImpl(FileStorageService fileStorageService, CsvParserService csvParserService, BalanceImportValidator balanceImportValidator, ImportJobRepository importJobRepository, BalanceImportRecordRepository balanceImportRecordRepository) {
        this.fileStorageService = fileStorageService;
        this.csvParserService = csvParserService;
        this.balanceImportValidator = balanceImportValidator;
        this.importJobRepository = importJobRepository;
        this.balanceImportRecordRepository = balanceImportRecordRepository;
    }

    @Override
    public void processImport(ImportMessage importMessage) {
        System.out.println("Processing import message: " + importMessage);
        ImportJob importJob = importJobRepository.findById(importMessage.getImportId())
                .orElseThrow(() -> new RuntimeException("Import job not found with ID: " + importMessage.getImportId()));
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

                BalanceImportRecordEntity entity = buildRecordEntity(record, importJob, ImportRecordStatus.SUCCESS, null);
                balanceImportRecordRepository.save(entity);
                successfulRecords++;

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
