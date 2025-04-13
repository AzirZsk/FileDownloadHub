package io.github.azirzsk.filedownloadhub.controller;

import io.github.azirzsk.filedownloadhub.exception.DownloadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author azir
 * @since 2025/04/13
 */
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DownloadException.class)
    public ResponseEntity<String> handleException(DownloadException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
