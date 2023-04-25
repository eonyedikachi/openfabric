package ai.openfabric.api.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionsHandler {

    @ExceptionHandler(InvalidFormatException.class)
    public static ResponseEntity<String> handleInvalidFormatException(InvalidFormatException e) {
        log.error("An error occurred while deserialization!:" + e.getMessage());
        return new ResponseEntity<>("An error occurred while deserialization, please check data types required for the request.",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public static ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("An error occurred!:" + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

        @ExceptionHandler(ConstraintViolationException.class)
        public static ResponseEntity<String> handleConstraintViolationException(
                ConstraintViolationException e) {
            log.error("Request not valid: " + e.getMessage());
            var error = "";
            for (ConstraintViolation<?> v : e.getConstraintViolations()) {
                error = v.getMessage();
                break;
            }
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public static ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
            log.error("Request not valid: " + e.getMessage());
            FieldError v = e.getFieldErrors().get(0);
            return new ResponseEntity<>(v.getField() + " failed with " + v.getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public static ResponseEntity<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
            log.error("Request not valid: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(RestClientResponseException.class)
        public static ResponseEntity<String> handleRestTemplateException(RestClientResponseException e) {
            log.error("An error occurred while communicating with an external api " + e.getStatusText());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public static ResponseEntity<String> handleDataIntegrityException(DataIntegrityViolationException e) {
            log.error("An error occurred while saving the data:" + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(DataNotFoundException.class)
        public static ResponseEntity<String> handleEventNotFoundException(DataNotFoundException e) {
            log.error("Data not found: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

