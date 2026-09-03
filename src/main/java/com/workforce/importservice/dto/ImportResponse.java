package com.workforce.importservice.dto;

import com.workforce.importservice.entity.ImportStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@Builder
public class ImportResponse {

    private Long importId;
    private String fileName;
    private ImportStatus status;
    private LocalDateTime createdAt;
}
