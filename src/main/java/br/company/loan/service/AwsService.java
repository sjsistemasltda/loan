package br.company.loan.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface AwsService {
    void send(String queueName, Object object) throws JsonProcessingException;
}
