package com.workforce.importservice.service;

import com.workforce.importservice.dto.FailedRecordResponse;
import com.workforce.importservice.dto.ImportResultResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {
   @Value("${aws.ses.from-email}")
    private String fromEmail;
    @Value("${aws.ses.to-email}")
    private String toEmail;

    private final SesClient sesClient;

    public EmailNotificationServiceImpl(SesClient sesClient){
        this.sesClient=sesClient;
    }
    @Override
    public void sendImportResultEmail(ImportResultResponse result) {

        String subject =
                "Balance Import Result - Import ID " + result.getImportId();

        String body = buildEmailBody(result);

        SendEmailRequest request = SendEmailRequest.builder()
                .source(fromEmail)
                .destination(
                        Destination.builder()
                                .toAddresses(toEmail)
                                .build()
                )
                .message(
                        Message.builder()
                                .subject(
                                        Content.builder()
                                                .data(subject)
                                                .build()
                                )
                                .body(
                                        Body.builder()
                                                .text(
                                                        Content.builder()
                                                                .data(body)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        sesClient.sendEmail(request);

        System.out.println(
                "Import result email sent successfully for import ID: "
                        + result.getImportId()
        );
    }

    private String buildEmailBody(ImportResultResponse result) {

        StringBuilder body = new StringBuilder();

        body.append("Employee Workforce Balance Import Result\n");
        body.append("========================================\n\n");

        body.append("Import ID: ")
                .append(result.getImportId())
                .append("\n");

        body.append("File Name: ")
                .append(result.getFileName())
                .append("\n");

        body.append("Status: ")
                .append(result.getStatus())
                .append("\n");

        body.append("Total Records: ")
                .append(result.getTotalRecords())
                .append("\n");

        body.append("Successful Records: ")
                .append(result.getSuccessfulRecords())
                .append("\n");

        body.append("Failed Records: ")
                .append(result.getFailedRecords())
                .append("\n");

        body.append("Completed At: ")
                .append(result.getCompletedAt())
                .append("\n\n");

        if (result.getFailedRecordDetails() != null
                && !result.getFailedRecordDetails().isEmpty()) {

            body.append("Failed Record Details\n");
            body.append("--------------------\n\n");

            for (FailedRecordResponse record :
                    result.getFailedRecordDetails()) {

                body.append("AAID: ")
                        .append(record.getAaid())
                        .append("\n");

                body.append("Balance Name: ")
                        .append(record.getBalanceName())
                        .append("\n");

                body.append("Action: ")
                        .append(record.getAction())
                        .append("\n");

                body.append("Balance Value: ")
                        .append(record.getBalanceValue())
                        .append("\n");

                body.append("Effective Date: ")
                        .append(record.getEffectiveDate())
                        .append("\n");

                body.append("Comments: ")
                        .append(record.getComments())
                        .append("\n");

                body.append("Status: ")
                        .append(record.getStatus())
                        .append("\n");

                body.append("Error: ")
                        .append(record.getErrorMessage())
                        .append("\n");

                body.append("\n--------------------\n\n");
            }

        } else {
            body.append("No failed records.\n");
        }

        return body.toString();
    }
}
