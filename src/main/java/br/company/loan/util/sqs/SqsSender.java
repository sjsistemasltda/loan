package br.company.loan.util.sqs;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.net.URI;

@Component
public class SqsSender {

    private final SqsClient sqsClient;

    public SqsSender() {
        this.sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .endpointOverride(URI.create("http://localhost:4566"))
                .build();
    }

    public void send(String queueName, String message) {
        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueName)
                .messageBody(message)
                .delaySeconds(5)
                .build();

        sqsClient.sendMessage(sendMessageRequest);
    }
}
