package com.sforce.profanitycheck.validation;

import com.sforce.profanitycheck.common.ApiResult;
import com.sforce.profanitycheck.wordlist.BannedWord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProfanityValidatorTests {

    @Test
    void shouldIdentifyValidFileContents() {
        List<BannedWord> bannedWords = Arrays.asList(
            new BannedWord(1L, "hell", null),
            new BannedWord(2L, "damn", null)
        );
        ApiResult result = new ApiResult();
        ProfanityValidator validator = new ProfanityValidator(bannedWords);

        MockMultipartFile validFile = new MockMultipartFile("validfile", "validfile.txt",
            "text/plain", "Some text which does not contain invalid words".getBytes());

        Assertions.assertThat(validator.validates(validFile, result)).isTrue();
    }

    @Test
    void shouldIdentifyInvalidFileContents() {
        List<BannedWord> bannedWords = Arrays.asList(
            new BannedWord(1L, "hell", null),
            new BannedWord(2L, "damn", null)
        );
        ApiResult result = new ApiResult();
        ProfanityValidator validator = new ProfanityValidator(bannedWords);

        // Contains invalid word "damn"
        MockMultipartFile invalidFile = new MockMultipartFile("invalidfile", "invalidfile.txt",
            "text/plain", "Some damn text which does contain invalid words".getBytes());
        Assertions.assertThat(validator.validates(invalidFile, result)).isFalse();

        // Contains 2 invalid words "hell" and "damn"
        invalidFile = new MockMultipartFile("invalidfile2", "invalidfile2.txt",
            "text/plain", "Some hell of a damn text".getBytes());
        Assertions.assertThat(validator.validates(invalidFile, result)).isFalse();

        // Contains "hell" and "damn" with mixed upper and lower case letters
        invalidFile = new MockMultipartFile("invalidfile3", "invalidfile3.txt",
            "text/plain", "Some hElL of a dAmN text".getBytes());
        Assertions.assertThat(validator.validates(invalidFile, result)).isFalse();

        // Contains "hell" within a word
        invalidFile = new MockMultipartFile("invalidfile4", "invalidfile4.txt",
            "text/plain", "she sells sea shells".getBytes());
        Assertions.assertThat(validator.validates(invalidFile, result)).isFalse();

        // Contains "hell" in the beginning of a word
        invalidFile = new MockMultipartFile("invalidfile5", "invalidfile5.txt",
            "text/plain", "hello world".getBytes());
        Assertions.assertThat(validator.validates(invalidFile, result)).isFalse();
    }

    @Test
    void shouldCheckValidFileNames() {
        List<BannedWord> bannedWords = Arrays.asList(
            new BannedWord(1L, "hell", null),
            new BannedWord(2L, "damn", null)
        );
        ApiResult result = new ApiResult();
        ProfanityValidator validator = new ProfanityValidator(bannedWords);

        MockMultipartFile validFile = new MockMultipartFile("somefile", "somefile.txt",
            "text/plain", "world".getBytes());

        Assertions.assertThat(validator.validates(validFile, result)).isTrue();
    }

    @Test
    void shouldCheckInvalidFileNames() {
        List<BannedWord> bannedWords = Arrays.asList(
            new BannedWord(1L, "hell", null),
            new BannedWord(2L, "damn", null)
        );
        ApiResult result = new ApiResult();
        ProfanityValidator validator = new ProfanityValidator(bannedWords);

        // Contains "hell" as filename
        MockMultipartFile invalidFile = new MockMultipartFile("hell", "hell.txt",
            "text/plain", "test".getBytes());
        Assertions.assertThat(validator.validates(invalidFile, result)).isFalse();

        // Contains "hell" as part of a file name
        invalidFile = new MockMultipartFile("shell", "shell.txt",
            "text/plain", "test".getBytes());
        Assertions.assertThat(validator.validates(invalidFile, result)).isFalse();

        // Contains "hell" in the beginning of a file name
        invalidFile = new MockMultipartFile("hello", "hello.txt",
            "text/plain", "world".getBytes());
        Assertions.assertThat(validator.validates(invalidFile, result)).isFalse();
    }
}
