package br.company.loan.service;

public interface AwsService {
    void send(String queueName, Object object);
}
