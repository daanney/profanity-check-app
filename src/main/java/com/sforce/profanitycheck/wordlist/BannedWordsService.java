package com.sforce.profanitycheck.wordlist;

import com.sforce.profanitycheck.common.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BannedWordsService {

    @Autowired
    private BannedWordsRepository repository;

    public Iterable<BannedWord> getAll() {
        return repository.findAll();
    }

    public BannedWord get(Long id) {
        return repository.findById(id).orElse(null);
    }

    public boolean addBannedWord(BannedWord bannedWord, ApiResult result) {
        if(bannedWord.getName().trim().isEmpty())
            return result.setFailure("The supplied word/phrase was not given");

        bannedWord.setName(bannedWord.getName().toLowerCase());

        if(repository.findByName(bannedWord.getName()).isPresent())
            return result.setFailure("This word/phrase already exists");

        repository.save(bannedWord);
        return result.setSuccess("Record created");
    }

    public boolean deleteWord(Long id, ApiResult result) {
        Optional<BannedWord> optBannedWord = repository.findById(id);
        if(optBannedWord.isPresent()) {
            repository.delete(optBannedWord.get());
            return result.setSuccess("Banned word removed successfully");
        }

        return result.setFailure("Record not found");
    }
}
