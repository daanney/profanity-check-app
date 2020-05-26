package com.sforce.profanitycheck.validation;

import com.sforce.profanitycheck.common.ApiResult;
import com.sforce.profanitycheck.wordlist.BannedWord;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

public class Validations {

    /**
     * List of basic validators
     */
    private static final List<Validator> validators = new LinkedList<>();

    // TODO: proper implementation to remove dependency to validators
    static {
        validators.add(new FileEmptyValidator());
        validators.add(new FileTypeValidator());
        validators.add(new FileSizeValidator());
    }

    private Validations() {}

    /**
     * Validates file against all validators and populates findings in result. if a failure is found in any validation,
     * the whole validation is interrupted and returned
     * @param file the attempted file upload
     * @param result the api response result
     * @param bannedWords list of banned words to be checked
     * @return true if the file is considered valid, false otherwise
     */
    public static boolean validatesAll(MultipartFile file, ApiResult result, List<BannedWord> bannedWords) {
        for(Validator validator : validators)
            if(! validator.validates(file, result))
                return false;

        // TODO: if there are more validators requiring banned words, then create a custom list for those
        ProfanityValidator profanityValidator = new ProfanityValidator(bannedWords);
        return profanityValidator.validates(file, result);
    }
}
