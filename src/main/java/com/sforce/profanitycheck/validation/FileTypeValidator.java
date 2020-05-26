package com.sforce.profanitycheck.validation;

import com.sforce.profanitycheck.common.ApiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileTypeValidator implements Validator {

    @Value("#{T(java.util.Arrays).asList('${profanitycheck.upload.allowedTypes:}')}")
    private List<String> allowedTypes;

    @Override
    public boolean validates(MultipartFile file, ApiResult result) {
        return allowedTypes != null && !allowedTypes.isEmpty() && !allowedTypes.contains(file.getContentType())
            ? result.setFailure("The type of the supplied file is not supported")
            : result.setSuccess("The file type is supported");
    }
}
