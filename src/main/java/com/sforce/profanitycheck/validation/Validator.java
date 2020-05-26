package com.sforce.profanitycheck.validation;

import com.sforce.profanitycheck.common.ApiResult;
import org.springframework.web.multipart.MultipartFile;

public interface Validator {

    /**
     * Abstract method to validate a file against certain rules.
     * @param file the uploaded file
     * @param result the object to write success/failure with respective message
     * @return true if the file validates correctly, false otherwise
     */
    boolean validates(MultipartFile file, ApiResult result);
}
