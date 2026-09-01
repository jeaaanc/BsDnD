package BankSdNd.example.BsDnD.adapter.in.web.handler;

import BankSdNd.example.BsDnD.core.domain.exception.BusinessException;
import BankSdNd.example.BsDnD.core.domain.exception.DuplicateException;
import BankSdNd.example.BsDnD.core.domain.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from DTOs (e.g., @NotBlank, @Size).
     * Returns HTTP 400 (Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("$$-Validation Error");
        problemDetail.setDetail("$$-One or more fields are invalid.");

        ex.getBindingResult().getFieldErrors().forEach(error ->
                problemDetail.setProperty(error.getField(), error.getDefaultMessage()));

        return problemDetail;
    }

    /**
     *
     * Handles cases where a resource is not found (e.g., User ID, Account Number).
     * Resturn HTTP 404 (Not Found).
     */
    @ExceptionHandler({UserNotFoundException.class, BankSdNd.example.BsDnD.core.domain.exception.AccountNotFoundException.class})
    public ProblemDetail hanldeUserNotFound(Exception ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setDetail(ex.getMessage());

        return problemDetail;
    }

    /**
     * Handles duplicate data conflicts (e.g., CPF or Phone Number already exists).
     * Returns HTTP 409 (Conflict).
     */
    @ExceptionHandler(DuplicateException.class)
    public ProblemDetail handleDuplicate(DuplicateException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Data Conflict");
        problemDetail.setDetail(ex.getMessage());

        return problemDetail;
    }

    /**
     * Catch-all handler for general business logic exceptions.
     * Returns HTTP 422 (Unprocessable Entity).
     */
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setDetail(ex.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDanied(AccessDeniedException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Access Denied");
        problemDetail.setDetail(ex.getMessage());

        return problemDetail;
    }

}
