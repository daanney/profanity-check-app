package com.sforce.profanitycheck.handlers;

import com.sforce.profanitycheck.common.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalUploadSizeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<ApiResult> handleSizeException() {
        ApiResult result = new ApiResult();
        result.setFailure("The supplied file is too large");
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
