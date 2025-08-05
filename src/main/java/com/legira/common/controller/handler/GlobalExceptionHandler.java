package com.legira.common.controller.handler;

import com.legira.common.controller.handler.exception.ExceptionResponseDTO;
import com.legira.common.security.jwt.exceptions.JwtExpiredException;
import com.legira.user.exceptions.EmailAlreadyExistsException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<ExceptionResponseDTO> handleJwtExpiredException(JwtExpiredException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildExceptionResponse(HttpStatus.UNAUTHORIZED, ex));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ExceptionResponseDTO> handleMalformedJwtException(MalformedJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildExceptionResponse(HttpStatus.BAD_REQUEST, ex));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ExceptionResponseDTO> handleSignatureException(SignatureException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildExceptionResponse(HttpStatus.UNAUTHORIZED, ex));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionResponseDTO> handleJwtException(JwtException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildExceptionResponse(HttpStatus.BAD_REQUEST, ex));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponseDTO> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildExceptionResponse(HttpStatus.CONFLICT, ex));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponseDTO> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildExceptionResponse(HttpStatus.BAD_REQUEST, ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDTO> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    private ExceptionResponseDTO buildExceptionResponse(HttpStatus status, Exception exception) {
        return ExceptionResponseDTO.builder()
                .statusCodeNumber(status.value())
                .statusCodeName(status.getReasonPhrase())
                .errorMessage(exception.getMessage())
                .build();
    }

}
