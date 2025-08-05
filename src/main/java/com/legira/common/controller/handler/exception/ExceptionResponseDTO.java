package com.legira.common.controller.handler.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ExceptionResponseDTO {
    private Integer statusCodeNumber;
    private String statusCodeName;
    private String errorMessage;
}
