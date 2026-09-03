package com.workforce.importservice.service;

import com.workforce.importservice.dto.ImportMessage;

public interface ImportProcessingService {
    void processImport(ImportMessage importMessage);
}
