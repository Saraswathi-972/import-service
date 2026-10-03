package com.workforce.importservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportRecordsPageResponse {
    private List<FailedRecordResponse> records;

    private int page;
    private int size;
    private long totalRecords;
    private int totalPages;
}
