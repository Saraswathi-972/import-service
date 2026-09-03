package com.workforce.importservice.service;

import com.workforce.importservice.entity.ImportJob;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileStorageService {

    public String uploadFile(MultipartFile file, String key);

    InputStream downloadFile(String key);
}
