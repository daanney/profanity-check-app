package com.sforce.profanitycheck;

import com.sforce.profanitycheck.common.CrudResult;
import com.sforce.profanitycheck.wordlist.BannedWord;
import com.sforce.profanitycheck.wordlist.BannedWordsRepository;
import com.sforce.profanitycheck.wordlist.BannedWordsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BannedWordsServiceTests {

    @Mock
    private BannedWordsRepository bannedWordsRepository;

    @InjectMocks
    private BannedWordsService service;

    @Test
    void shouldReturnAllWordsCorrectly() {
        List<BannedWord> data = Arrays.asList(
            new BannedWord(1L, "first", "description"),
            new BannedWord(2L, "second", "other description")
        );
        BDDMockito.given(bannedWordsRepository.findAll()).willReturn(data);
        Assertions.assertThat(service.getAll()).isEqualTo(data);
    }

    @Test
    void shouldReturnNullIfIdNotFound() {
        long id = 13L;
        Assertions.assertThat(service.get(id)).isNull();
    }

    @Test
    void shouldAddNewBannedWord() {
        BannedWord word = new BannedWord("valid phrase");
        CrudResult result = service.addBannedWord(word);
        Assertions.assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldRevokeEmptyNames() {
        BannedWord word = new BannedWord("");
        CrudResult result = service.addBannedWord(word);
        Assertions.assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void shouldNotAddDuplicateWords() {
        BannedWord word = new BannedWord("some phrase");
        BDDMockito.given(bannedWordsRepository.findByName(word.getName())).willReturn(Optional.of(word));
        CrudResult result = service.addBannedWord(word);
        Assertions.assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void shouldDeleteWordCorrectly() {
        BannedWord word = new BannedWord(1L, "to be", "deleted");
        BDDMockito.given(bannedWordsRepository.findById(word.getId())).willReturn(Optional.of(word));
        CrudResult result = service.deleteWord(word.getId());
        Assertions.assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldHandleUnknownIdInDeletion() {
        BannedWord word = new BannedWord(1337L, "to be", "deleted");
        BDDMockito.given(bannedWordsRepository.findById(word.getId())).willReturn(Optional.empty());
        CrudResult result = service.deleteWord(word.getId());
        Assertions.assertThat(result.isSuccess()).isFalse();
    }
}
