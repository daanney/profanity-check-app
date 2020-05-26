package com.sforce.profanitycheck;

import com.sforce.profanitycheck.document.Document;
import com.sforce.profanitycheck.document.DocumentsRepository;
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
