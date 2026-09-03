package com.workforce.importservice.service;

import com.workforce.importservice.dto.ImportMessage;

public interface SqsMessageService {

    public void sendMessage(ImportMessage message);
}
