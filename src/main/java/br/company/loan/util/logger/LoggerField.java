package br.company.loan.util.logger;

import lombok.Getter;

@Getter
public enum LoggerField {
    CORRELATION_ID("correlationId"),
    PAYLOAD("payload");

    private final String name;

    LoggerField(String name) {
        this.name = name;
    }

}
