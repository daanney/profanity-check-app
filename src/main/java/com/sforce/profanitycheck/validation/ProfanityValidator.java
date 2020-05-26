package com.sforce.profanitycheck.validation;

import com.sforce.profanitycheck.common.ApiResult;
import com.sforce.profanitycheck.wordlist.BannedWord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProfanityValidator implements Validator {

    final private List<BannedWord> bannedWords;

    public ProfanityValidator(List<BannedWord> bannedWords) {
        this.bannedWords = bannedWords;
    }

    /**
     * Reads the supplied file content and verifies it against the banned words check function. To be called after
     * basic verifications
     * @param file the uploaded file
     * @param result result holding success and message of the process
     * @return true if considered to contain banned words, false otherwise
     */
    public boolean validates(MultipartFile file, ApiResult result) {
        boolean containsBanned = false;

        if(dataContainsBannedWord(file.getOriginalFilename(), bannedWords)) {
            containsBanned = true;
        }else { // only process the file content if the file name already signaled alert
            try {
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                    String line;
                    while(!containsBanned && (line = reader.readLine()) != null)
                        if(dataContainsBannedWord(line, bannedWords)) containsBanned = true;
                }
            }catch(IOException e) {
                // TODO: update result
                e.printStackTrace();
            }
        }

        // TODO: stats, signals, ..?

        return containsBanned
            ? result.setFailure("The supplied file contains blocked content and has been discarded.")
            : result.setSuccess("The supplied file passed the profanity check");
    }

    /**
     * Simple word check done on data using the provided banned words. The data is transformed with stripping non-word
     * characters and normalizing to lower case.
     * TODO: use conventional library?
     * @param data a string to be verified
     * @param bannedWords list of banned words to be checked on data
     * @return true of data contains any of the provided banned words, false otherwise
     */
    public boolean dataContainsBannedWord(String data, List<BannedWord> bannedWords) {
        if(null == data || null == bannedWords) return false; // TODO: exception handling

        data = data.toLowerCase().replaceAll("[^a-zA-Z]", "");
        for(BannedWord bannedWord : bannedWords) {
            if(data.contains(bannedWord.getName())) return true;
        }

        return false;
    }
}
