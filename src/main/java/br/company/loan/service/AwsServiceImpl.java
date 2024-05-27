package br.company.loan.service;

import br.company.loan.controller.exception.ProcessPaymentException;
import br.company.loan.util.sqs.SqsSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AwsServiceImpl implements AwsService {

    private final SqsSender sqsSender;

    @Override
    public void send(String queueName, Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(object);

            sqsSender.send(queueName, json);
        } catch (JsonProcessingException e) {
            throw new ProcessPaymentException("Process payment with error");
        }

    }
}
