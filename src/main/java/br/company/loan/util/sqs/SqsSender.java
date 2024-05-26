package br.company.loan.util.sqs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.net.URI;

@Component
public class SqsSender {

    private final SqsClient sqsClient;

    public SqsSender(
            @Value("${localstack.url}") String localstackUrl,
            @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey,
            @Value("${spring.cloud.aws.credentials.access-key}") String accessKey
    ) {
        this.sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        accessKey,
                        secretKey
                )))
                .endpointOverride(URI.create(localstackUrl))
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
