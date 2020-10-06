package guru.springframework.msscbrewery.handler;

import guru.springframework.msscbrewery.handler.error.BadRequestError;
import guru.springframework.msscbrewery.handler.error.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String CORRELATION_ID = "correlation-id";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = "Parameter validation error :" + ex.getBindingResult().getFieldErrors().toString();
        Error error = new Error(HttpStatus.BAD_REQUEST);
        error.setCode(400);
        error.setMessage("Validation Error");
        error.addValidationErrors(ex.getBindingResult().getFieldErrors());

        errorMessage = errorMessage + getCorrelationId(request);
        return buildResponseEntity(error);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

            String errorMessage = "Bad Request. Invalid Input.";
            Error error = new Error(HttpStatus.BAD_REQUEST);
            error.setCode(400);
            error.setMessage(errorMessage);
            error.addErrors(new BadRequestError(errorMessage));

            errorMessage = errorMessage + getCorrelationId(request);
            return buildResponseEntity(error);

    }

    protected String getCorrelationId(WebRequest request) {
        return Optional.ofNullable(request.getHeader(CORRELATION_ID)).map(value -> value + " : ").orElse("");
    }

    protected ResponseEntity<Object> buildResponseEntity(Error error) {
        return new ResponseEntity<>(error, error.getStatus());
    }
}
