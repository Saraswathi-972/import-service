package com.workforce.importservice.service;

import com.workforce.importservice.dto.ImportRecordsPageResponse;
import com.workforce.importservice.dto.ImportResultResponse;
import org.springframework.data.domain.Pageable;

public interface ImportResultService {
    public ImportResultResponse getImportResults(Long importId);
    ImportRecordsPageResponse getImportRecords(
            Long importId,
            Pageable pageable
    );
}
