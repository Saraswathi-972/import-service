package com.workforce.importservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImportMessage {
    private String s3Key;
    private Long importId;

}
