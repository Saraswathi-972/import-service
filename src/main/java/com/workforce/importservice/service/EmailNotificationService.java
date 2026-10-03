package com.workforce.importservice.service;

import com.workforce.importservice.dto.ImportResultResponse;

public interface EmailNotificationService {
    void sendImportResultEmail(ImportResultResponse result);
}
