package explore.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class MainExceptionHandler {
    @ExceptionHandler(NotFoundError.class)
    public ResponseEntity<ApiError> handleNotFoundError(NotFoundError e) {
        log.warn("NotFoundError: {}", e.getMessage());

        ApiError error = new ApiError(e.getMessage(), e.getReason());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String violations = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList())
                .toString();
        ApiError error = new ApiError(violations, "Incorrectly made request");

        log.warn("Ошибка валидации MethodArgumentNotValidException: {}.", violations);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectRequestError.class)
    public ResponseEntity<ApiError> handleIncorrectRequestError(IncorrectRequestError e) {
        log.warn("IncorrectRequestError: {}", e.getMessage());

        ApiError error = new ApiError(e.getMessage(), e.getReason());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("DataIntegrityViolationException: {}" + e.getMessage());

        ApiError error = new ApiError(e.getMessage(), "Integrity constraint has been violated");

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DateRequestException.class)
    public ResponseEntity<ApiError> handleIncorrectDateRequest(DateRequestException e) {
        log.warn("DateRequestException: {}" + e.getMessage());

        ApiError error = new ApiError(e.getMessage(), e.getReason());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
