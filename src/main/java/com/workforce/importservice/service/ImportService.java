package com.workforce.importservice.service;

import com.workforce.importservice.dto.ImportResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    ImportResponse createImport(MultipartFile file);
}
