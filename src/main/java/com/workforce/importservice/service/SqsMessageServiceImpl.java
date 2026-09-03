package com.workforce.importservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workforce.importservice.dto.ImportMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class SqsMessageServiceImpl implements SqsMessageService{
    @Value("${aws.sqs.queue-url}")
    private String queueUrl;
    private final SqsClient sqsClient;
    private ObjectMapper objectMapper;

    public SqsMessageServiceImpl(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendMessage(ImportMessage importMessage) {
        try {
            String messageBody = objectMapper.writeValueAsString(importMessage);
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();
            sqsClient.sendMessage(sendMsgRequest);
        }
        catch (JsonProcessingException e){
            throw new RuntimeException("Failed to create SQS message", e);
        }
    }
}
