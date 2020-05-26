package com.sforce.profanitycheck.validation;

import com.sforce.profanitycheck.common.ApiResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileEmptyValidator implements Validator {

    @Override
    public boolean validates(MultipartFile file, ApiResult result) {
        return (null == file || file.isEmpty())
            ? result.setFailure("The supplied file is empty")
            : result.setSuccess("The file is not empty");
    }
}
