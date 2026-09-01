package explore.exceptions;

import exceptions.ErrorResponse;
import exceptions.NotFoundError;
import exceptions.ResourceAlreadyExistsError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(NotFoundError.class)
    public ResponseEntity<ErrorResponse> handleNotFoundError(NotFoundError e) {
        log.warn("NotFoundError: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsError.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsError(ResourceAlreadyExistsError e) {
        log.warn("ResourceAlreadyExistsError: {}.", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String violations = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList())
                .toString();

        log.warn("Ошибка валидации MethodArgumentNotValidException: {}.", violations);
        return new ResponseEntity<>(new ErrorResponse(violations), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable e) {
        log.error("Критическая ошибка: {}.", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
