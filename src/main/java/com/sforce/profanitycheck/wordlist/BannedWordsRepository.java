package com.sforce.profanitycheck.wordlist;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BannedWordsRepository extends CrudRepository<BannedWord, Long> {

    Optional<BannedWord> findByName(String name);
}
