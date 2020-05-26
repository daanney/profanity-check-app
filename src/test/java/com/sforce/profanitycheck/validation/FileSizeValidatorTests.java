package com.sforce.profanitycheck.validation;

import com.sforce.profanitycheck.common.ApiResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class FileSizeValidatorTests {

    @Test
    void shouldValidateSizeCorrectly() {
        MockMultipartFile file = new MockMultipartFile("file", "file.txt",
            "text/plain", "Some text".getBytes());

        ApiResult result = new ApiResult();
        FileSizeValidator validator = new FileSizeValidator();
        ReflectionTestUtils.setField(validator, "maxFileSize", 10);
        validator.validates(file, result);
        Assertions.assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldCatchLargeFiles() {
        MockMultipartFile file = new MockMultipartFile("bigfile", "bigfile.txt",
            "text/plain", "Some text that is too large".getBytes());

        ApiResult result = new ApiResult();
        FileSizeValidator validator = new FileSizeValidator();
        ReflectionTestUtils.setField(validator, "maxFileSize", 20);
        validator.validates(file, result);
        Assertions.assertThat(result.isSuccess()).isFalse();
    }
}
