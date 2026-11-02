package com.moviereservation.booking.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleStatus(ResponseStatusException e, HttpServletRequest request) {
        HttpStatusCode status = e.getStatusCode();
        String detail = e.getReason() != null ? e.getReason() : "Request could not be completed";
        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        return buildResponse(status, httpStatus != null ? httpStatus.getReasonPhrase() : "Request Failed", detail, request);
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ProblemDetail> handleNotFound(HttpClientErrorException.NotFound e, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", "Showtime not found", request);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ProblemDetail> handleServiceUnavailable(ResourceAccessException e, HttpServletRequest request) {
        log.error("Could not connect to Movie Service", e);
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "Movie Service is currently unavailable", request);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ProblemDetail> handleRestClientError(RestClientResponseException e, HttpServletRequest request) {
        log.error("Movie Service returned HTTP {}", e.getStatusCode());
        return buildResponse(HttpStatus.BAD_GATEWAY, "Bad Gateway", "An error occurred while communicating with Movie Service", request);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateKey(DuplicateKeyException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", "A record with the same unique values already exists", request);
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ProblemDetail> handleDatabaseUnavailable(DataAccessResourceFailureException e, HttpServletRequest request) {
        log.error("Database connection failure", e);
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "Database is currently unavailable", request);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ProblemDetail> handleDatabase(DataAccessException e, HttpServletRequest request) {
        log.error("Database operation failed", e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "A database operation failed", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        ProblemDetail problem = createProblem(HttpStatus.BAD_REQUEST, "Bad Request", "Validation failed", request);
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadableMessage(HttpMessageNotReadableException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Invalid or malformed request body", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", "HTTP method is not supported for this endpoint", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleMissingEndpoint(NoResourceFoundException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", "Endpoint not found", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception e, HttpServletRequest request) {
        log.error("Unexpected error at {}", request.getRequestURI(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred", request);
    }

    private ResponseEntity<ProblemDetail> buildResponse(HttpStatusCode status, String title, String detail, HttpServletRequest request) {
        return ResponseEntity.status(status).body(createProblem(status, title, detail, request));
    }

    private ProblemDetail createProblem(HttpStatusCode status, String title, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}