package com.workforce.importservice.dto;

import com.workforce.importservice.entity.ImportStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultResponse {

    private Long importId;
    private String fileName;
    private ImportStatus status;

    private Integer totalRecords;
    private Integer successfulRecords;
    private Integer failedRecords;

    private LocalDateTime completedAt;

    private List<FailedRecordResponse> failedRecordDetails;
}