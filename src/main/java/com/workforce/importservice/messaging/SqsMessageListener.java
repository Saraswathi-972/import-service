package com.workforce.importservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workforce.importservice.dto.ImportMessage;
import com.workforce.importservice.service.ImportProcessingService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SqsMessageListener {
    private final ObjectMapper objectMapper;
    private final ImportProcessingService importProcessingService;

    public SqsMessageListener(ObjectMapper objectMapper, ImportProcessingService importProcessingService) {
        this.importProcessingService = importProcessingService;
        this.objectMapper = objectMapper;
    }

    @SqsListener("${aws.sqs.queue-name}")
    public void receiveMessage(String message) {
        try{
            ImportMessage importMessage = objectMapper.readValue(message, ImportMessage.class);
            System.out.println("Received message: " + message);
            System.out.println("ImportId: "+importMessage.getImportId());
            System.out.println("FileName: "+importMessage.getS3Key());
            importProcessingService.processImport(importMessage);
        }catch (Exception e){
            System.err.println("Failed to parse message: " + e.getMessage());
            throw new RuntimeException("Failed to process import message", e);
        }


    }
}
