package com.workforce.importservice.service;

import com.netflix.discovery.provider.Serializer;
import com.workforce.importservice.dto.FailedRecordResponse;
import com.workforce.importservice.dto.ImportRecordsPageResponse;
import com.workforce.importservice.dto.ImportResultResponse;
import com.workforce.importservice.entity.BalanceImportRecordEntity;
import com.workforce.importservice.entity.ImportJob;
import com.workforce.importservice.entity.ImportRecordStatus;
import com.workforce.importservice.repository.BalanceImportRecordRepository;
import com.workforce.importservice.repository.ImportJobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImportResultServiceImpl implements ImportResultService{

        private final ImportJobRepository importJobRepository;
        private final BalanceImportRecordRepository balanceImportRecordRepository;

    public ImportResultServiceImpl(ImportJobRepository importJobRepository, BalanceImportRecordRepository balanceImportRecordRepository) {
            this.importJobRepository = importJobRepository;
            this.balanceImportRecordRepository = balanceImportRecordRepository;
        }

    @Override
    public ImportResultResponse getImportResults(Long importId) {

        // 1. Find import job
        ImportJob importJob = importJobRepository.findById(importId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Import job not found with ID: " + importId
                        ));

        // 2. Find all failed records
        List<BalanceImportRecordEntity> failedRecords =
                balanceImportRecordRepository
                        .findByImportJobIdAndImportRecordStatus(
                                importId,
                                ImportRecordStatus.FAILED
                        );

        // 3. Convert failed records to response DTO
        List<FailedRecordResponse> failedRecordDetails =
                failedRecords.stream()
                        .map(this::mapToFailedRecordResponse)
                        .toList();

        // 4. Build final response
        return ImportResultResponse.builder()
                .importId(importJob.getId())
                .fileName(importJob.getFileName())
                .status(importJob.getStatus())
                .totalRecords(importJob.getTotalRecords())
                .successfulRecords(importJob.getSuccessfulRecords())
                .failedRecords(importJob.getFailedRecords())
                .completedAt(importJob.getCompletedAt())
                .failedRecordDetails(failedRecordDetails)
                .build();
    }

    @Override
    public ImportRecordsPageResponse getImportRecords(
            Long importId,
            Pageable pageable) {

        // 1. Verify import exists
        importJobRepository.findById(importId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Import job not found with ID: " + importId
                        ));

        // 2. Fetch failed records page by page
        Page<BalanceImportRecordEntity> failedRecords =
                balanceImportRecordRepository
                        .findByImportJobIdAndImportRecordStatus(
                                importId,
                                ImportRecordStatus.FAILED,
                                 pageable
                        );

        // 3. Convert entities to response DTOs
        List<FailedRecordResponse> records =
                failedRecords.getContent()
                        .stream()
                        .map(this::mapToFailedRecordResponse)
                        .toList();

        // 4. Build paginated response
        return ImportRecordsPageResponse.builder()
                .records(records)
                .page(failedRecords.getNumber())
                .size(failedRecords.getSize())
                .totalRecords(failedRecords.getTotalElements())
                .totalPages(failedRecords.getTotalPages())
                .build();
    }

    private FailedRecordResponse mapToFailedRecordResponse(
            BalanceImportRecordEntity record) {

        return FailedRecordResponse.builder()
                .aaid(record.getAaid())
                .balanceName(record.getBalanceName())
                .action(record.getAction())
                .balanceValue(record.getBalanceValue())
                .effectiveDate(record.getEffectiveDate())
                .comments(record.getComments())
                .status(record.getImportRecordStatus().name())
                .errorMessage(record.getErrorMessage())
                .build();
    }

}
