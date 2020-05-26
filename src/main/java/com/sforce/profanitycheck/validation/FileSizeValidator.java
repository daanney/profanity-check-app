package com.sforce.profanitycheck.validation;

import com.sforce.profanitycheck.common.ApiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements Validator {

    @Value("${profanitycheck.upload.maxSize}")
    private long maxFileSize;

    @Override
    public boolean validates(MultipartFile file, ApiResult result) {
        return maxFileSize > 0 && file.getSize() > maxFileSize
            ? result.setFailure("The supplied file is too large")
            : result.setSuccess("The file size is ok");
    }
}
