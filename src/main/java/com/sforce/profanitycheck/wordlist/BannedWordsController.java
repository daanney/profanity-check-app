package com.sforce.profanitycheck.wordlist;

import com.sforce.profanitycheck.common.ApiResult;
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
    public ResponseEntity<ApiResult> create(@RequestBody BannedWord bannedWord) {
        ApiResult result = new ApiResult();
        if(service.addBannedWord(bannedWord, result))
            return new ResponseEntity<>(result, HttpStatus.OK);

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResult> delete(@PathVariable Long id) {
        ApiResult result = new ApiResult();
        if(service.deleteWord(id, result))
            return new ResponseEntity<>(result, HttpStatus.OK);

        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }
}
