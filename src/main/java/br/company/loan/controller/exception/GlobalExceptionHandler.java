package br.company.loan.controller.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonAlreadyExistsException.class)
    public ResponseEntity<?> handlePersonAlreadyExistsException(PersonAlreadyExistsException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            InvalidIdentifierException.class,
            PersonNotFoundException.class,
            ProcessPaymentException.class,
            MaxAmountLoanException.class,
            MaxInvoiceQuantityException.class,
            MinAmountLoanException.class
    })
    public ResponseEntity<?> handleInvalidExceptionBadRequest(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errorMessages = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        String message = String.join(", ", errorMessages);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, request.getDescription(false));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        Throwable mostSpecificCause = ex.getMostSpecificCause();
        if (mostSpecificCause instanceof UnrecognizedPropertyException unrecognizedPropertyException) {

            List<JsonMappingException.Reference> path = unrecognizedPropertyException.getPath();
            String unrecognizedFields = path.stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining(", "));

            String errorMessage = "Unrecognized field: " + unrecognizedFields;
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage, request.getDescription(false));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Internal error", request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class ErrorResponse {
    private int status;
    private String message;
    private String path;
}