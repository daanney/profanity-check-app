package com.sforce.profanitycheck.wordlist;

import com.sforce.profanitycheck.common.CrudResult;
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

    public CrudResult addBannedWord(BannedWord bannedWord) {
        CrudResult result = new CrudResult();
        if(bannedWord.getName().trim().isEmpty())
            return result.setFailure("The supplied word/phrase was not given");

        bannedWord.setName(bannedWord.getName().toLowerCase());

        if(repository.findByName(bannedWord.getName()).isPresent())
            return result.setFailure("This word/phrase already exists");

        repository.save(bannedWord);
        return result.setSuccess("Record created");
    }

    public CrudResult deleteWord(Long id) {
        CrudResult result = new CrudResult();
        Optional<BannedWord> optBannedWord = repository.findById(id);
        if(optBannedWord.isPresent()) {
            repository.delete(optBannedWord.get());
            return result.setSuccess("Banned word removed successfully");
        }

        return result.setFailure("Record not found");
    }
}
