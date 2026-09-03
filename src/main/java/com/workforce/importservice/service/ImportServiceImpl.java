package com.workforce.importservice.service;

import com.workforce.importservice.dto.ImportMessage;
import com.workforce.importservice.dto.ImportResponse;
import com.workforce.importservice.entity.ImportJob;
import com.workforce.importservice.entity.ImportStatus;
import com.workforce.importservice.repository.ImportJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImportServiceImpl implements ImportService {

    private final FileStorageService fileStorageService;
    private final ImportJobRepository importJobRepository;
    private final SqsMessageService sqsMessageService;

    public ImportServiceImpl(
            FileStorageService fileStorageService,
            ImportJobRepository importJobRepository,
            SqsMessageService sqsMessageService) {

        this.fileStorageService = fileStorageService;
        this.importJobRepository = importJobRepository;
        this.sqsMessageService = sqsMessageService;
    }

    @Override
    public ImportResponse createImport(MultipartFile file) {

        String key = "imports/" +
                LocalDate.now() + "/" +
                UUID.randomUUID() + "-" +
                file.getOriginalFilename();

        // 1. Upload file to S3
        String s3Key = fileStorageService.uploadFile(file, key);

        // 2. Create ImportJob
        ImportJob importJob = ImportJob.builder()
                .fileName(file.getOriginalFilename())
                .s3Key(s3Key)
                .status(ImportStatus.UPLOADED)
                .createdAt(LocalDateTime.now())
                .totalRecords(0)
                .successfulRecords(0)
                .failedRecords(0)
                .build();

        // 3. Save ImportJob to MySQL
        ImportJob savedJob = importJobRepository.save(importJob);

        // 4. Send message to SQS
        ImportMessage message = ImportMessage.builder()
                .importId(savedJob.getId())
                .s3Key(savedJob.getS3Key())
                .build();

        sqsMessageService.sendMessage(message);

        // 5. Return response
        return ImportResponse.builder()
                .importId(savedJob.getId())
                .fileName(savedJob.getFileName())
                .status(savedJob.getStatus())
                .createdAt(savedJob.getCreatedAt())
                .build();
    }
}