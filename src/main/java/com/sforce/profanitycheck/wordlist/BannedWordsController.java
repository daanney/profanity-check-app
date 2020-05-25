package com.sforce.profanitycheck.wordlist;

import com.sforce.profanitycheck.common.CrudResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("bannedwords")
public class BannedWordsController {

    @Autowired
    private BannedWordsService service;

    @GetMapping
    public Iterable<BannedWord> getAll() {
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<CrudResult> create(@RequestBody BannedWord bannedWord) {
        CrudResult result = service.addBannedWord(bannedWord);
        if(result.isSuccess()) return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<CrudResult> delete(@PathVariable Long id) {
        CrudResult result = service.deleteWord(id);
        if(result.isSuccess()) return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }
}
