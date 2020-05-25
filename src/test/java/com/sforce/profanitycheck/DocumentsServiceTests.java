package com.sforce.profanitycheck;

import com.sforce.profanitycheck.document.Document;
import com.sforce.profanitycheck.document.DocumentsRepository;
import com.sforce.profanitycheck.common.CrudResult;
import com.sforce.profanitycheck.document.DocumentsService;
import com.sforce.profanitycheck.wordlist.BannedWord;
import com.sforce.profanitycheck.wordlist.BannedWordsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DocumentsServiceTests {

    @Mock
    private BannedWordsRepository bannedWordsRepository;

    @Mock
    private DocumentsRepository documentsRepository;

    @InjectMocks
    private DocumentsService documentsService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(documentsService, "uploadDirectory", "/tmp/");
        ReflectionTestUtils.setField(documentsService, "maxFileSize", 20);
        ReflectionTestUtils.setField(documentsService, "allowedTypes", Arrays.asList("text/plain"));
    }

    void mockBannedWords() {
        List<BannedWord> data = Arrays.asList(
            new BannedWord(1L, "hell", null),
            new BannedWord(2L, "damn", null)
        );

        BDDMockito.given(bannedWordsRepository.findAll()).willReturn(data);
    }

    @Test
    void shouldCatchEmptyFiles() {
        MockMultipartFile file = new MockMultipartFile("file", "file.txt",
            "text/plain", "".getBytes());

        CrudResult result = new CrudResult();
        documentsService.validateFile(file, result);
        Assertions.assertThat(result.isSuccess()).isFalse();
}

    @Test
    void shouldCatchLargeFiles() {
        MockMultipartFile file = new MockMultipartFile("bigfile", "bigfile.txt",
            "text/plain", "Some text that is too large".getBytes());

        CrudResult result = new CrudResult();
        documentsService.validateFile(file, result);
        Assertions.assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void shouldCatchUnsupportedFiles() {
        MockMultipartFile file = new MockMultipartFile("invalidfile", "invalidfile.txt",
            "application/json", "{\"var\":\"val\"}".getBytes());

        CrudResult result = new CrudResult();
        documentsService.validateFile(file, result);
        Assertions.assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void shouldValidateFileSuccessfully() {
        MockMultipartFile file = new MockMultipartFile("validfile", "validfile.txt",
            "text/plain", "Some text".getBytes());

        CrudResult result = new CrudResult();
        documentsService.validateFile(file, result);
        Assertions.assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldIdentifyValidFileContents() {
        mockBannedWords();

        MockMultipartFile validFile = new MockMultipartFile("validfile", "validfile.txt",
            "text/plain", "Some text which does not contain invalid words".getBytes());
        Assertions.assertThat(documentsService.fileContainsBannedWords(validFile)).isFalse();
    }

    @Test
    void shouldIdentifyInvalidFileContents() {
        mockBannedWords();

        // Contains invalid word "damn"
        MockMultipartFile invalidFile = new MockMultipartFile("invalidfile", "invalidfile.txt",
            "text/plain", "Some damn text which does contain invalid words".getBytes());
        Assertions.assertThat(documentsService.fileContainsBannedWords(invalidFile)).isTrue();

        // Contains 2 invalid words "hell" and "damn"
        invalidFile = new MockMultipartFile("invalidfile2", "invalidfile2.txt",
            "text/plain", "Some hell of a damn text".getBytes());
        Assertions.assertThat(documentsService.fileContainsBannedWords(invalidFile)).isTrue();

        // Contains "hell" and "damn" with mixed upper and lower case letters
        invalidFile = new MockMultipartFile("invalidfile3", "invalidfile3.txt",
            "text/plain", "Some hElL of a dAmN text".getBytes());
        Assertions.assertThat(documentsService.fileContainsBannedWords(invalidFile)).isTrue();

        // Contains "hell" within a word
        invalidFile = new MockMultipartFile("invalidfile4", "invalidfile4.txt",
            "text/plain", "she sells sea shells".getBytes());
        Assertions.assertThat(documentsService.fileContainsBannedWords(invalidFile)).isTrue();

        // Contains "hell" in the beginning of a word
        invalidFile = new MockMultipartFile("invalidfile5", "invalidfile5.txt",
            "text/plain", "hello world".getBytes());
        Assertions.assertThat(documentsService.fileContainsBannedWords(invalidFile)).isTrue();
    }

    @Test
    void shouldCheckValidFileNames() {
        mockBannedWords();

        MockMultipartFile validFile = new MockMultipartFile("somefile", "somefile.txt",
            "text/plain", "world".getBytes());
        Assertions.assertThat(documentsService.fileContainsBannedWords(validFile)).isFalse();
    }

    @Test
    void shouldCheckInvalidFileNames() {
        mockBannedWords();

        // Contains "hell" as filename
        MockMultipartFile invalidFile = new MockMultipartFile("hell", "hell.txt",
            "text/plain", "test".getBytes());
        Assertions.assertThat(documentsService.fileContainsBannedWords(invalidFile)).isTrue();

        // Contains "hell" as part of a file name
        invalidFile = new MockMultipartFile("shell", "shell.txt",
            "text/plain", "test".getBytes());
        Assertions.assertThat(documentsService.fileContainsBannedWords(invalidFile)).isTrue();

        // Contains "hell" in the beginning of a file name
        invalidFile = new MockMultipartFile("hello", "hello.txt",
            "text/plain", "world".getBytes());
        Assertions.assertThat(documentsService.fileContainsBannedWords(invalidFile)).isTrue();
    }

    @Test
    void shouldSaveNewDocumentMetadata() {
        String filename = "somefile";
        String type = "text/plain";
        long size = 123L;
        Document doc = new Document(filename, type, size);
        Assertions.assertThat(documentsService.saveNewDocument(filename, type, size).getName()).isEqualTo(doc.getName());
    }

    @Test
    void shouldSaveNewDocumentMetadataWithExistingName() {
        String filename = "somefile.txt";
        String type = "text/plain";
        long size = 123L;
        BDDMockito.given(documentsRepository.findByName(filename)).willReturn(Optional.of(new Document(filename, type, size)));
        Document testDoc = documentsService.saveNewDocument(filename, type, size);
        // The name should be different, esp. longer
        Assertions.assertThat(testDoc.getName()).isNotEqualTo(filename);
        Assertions.assertThat(testDoc.getName().length()).isGreaterThan(filename.length());
    }
}
