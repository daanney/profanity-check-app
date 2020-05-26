package com.sforce.profanitycheck.validation;

import com.sforce.profanitycheck.common.ApiResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class FileTypeValidatorTests {

    @Test
    void shouldValidateTypeCorrectly() {
        MockMultipartFile file = new MockMultipartFile("file", "file.txt",
            "text/plain", "Some text".getBytes());

        ApiResult result = new ApiResult();
        FileTypeValidator validator = new FileTypeValidator();
        ReflectionTestUtils.setField(validator, "allowedTypes", Arrays.asList("text/plain"));
        validator.validates(file, result);
        Assertions.assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldCatchUnsupportedFiles() {
        MockMultipartFile file = new MockMultipartFile("invalidfile", "invalidfile.txt",
            "application/json", "{\"var\":\"val\"}".getBytes());

        ApiResult result = new ApiResult();
        FileTypeValidator validator = new FileTypeValidator();
        ReflectionTestUtils.setField(validator, "allowedTypes", Arrays.asList("text/plain"));
        validator.validates(file, result);
        Assertions.assertThat(result.isSuccess()).isFalse();
    }
}
