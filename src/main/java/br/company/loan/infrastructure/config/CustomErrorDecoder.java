package br.company.loan.infrastructure.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        return switch (status) {
            case BAD_REQUEST -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
            case NOT_FOUND -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found");
            case INTERNAL_SERVER_ERROR ->
                    new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}
