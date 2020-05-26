package com.sforce.profanitycheck.validation;

import com.sforce.profanitycheck.common.ApiResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class FileEmptyValidatorTests {

    @Test
    void shouldCatchEmptyFiles() {
        MockMultipartFile file = new MockMultipartFile("file", "file.txt",
            "text/plain", "".getBytes());

        ApiResult result = new ApiResult();
        FileEmptyValidator validator = new FileEmptyValidator();
        validator.validates(file, result);
        Assertions.assertThat(result.isSuccess()).isFalse();

        validator.validates(null, result);
        Assertions.assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void shouldValidateFileSuccessfully() {
        MockMultipartFile file = new MockMultipartFile("validfile", "validfile.txt",
            "text/plain", "Some text".getBytes());

        ApiResult result = new ApiResult();
        FileEmptyValidator validator = new FileEmptyValidator();
        validator.validates(file, result);
        Assertions.assertThat(result.isSuccess()).isTrue();
    }
}
