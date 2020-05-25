package com.sforce.profanitycheck.document;

import com.sforce.profanitycheck.common.CrudResult;
import com.sforce.profanitycheck.wordlist.BannedWord;
import com.sforce.profanitycheck.wordlist.BannedWordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service

public class DocumentsService {

    @Value("${profanitycheck.upload.maxSize}")
    private long maxFileSize;

    @Value("#{T(java.util.Arrays).asList('${profanitycheck.upload.allowedTypes:}')}")
    private List<String> allowedTypes;

    @Value("${profanitycheck.upload.dir}")
    private String uploadDirectory;

    @Autowired
    BannedWordsRepository bannedWordsRepository;

    @Autowired
    DocumentsRepository documentsRepository;

    public Iterable<Document> getDocuments() {
        return documentsRepository.findAll();
    }

    public Document getDocument(Long id) {
        return documentsRepository.findById(id).orElse(null);
    }

    /**
     * Uploads a file to specified directory and metadata stored in database, if it is considered valid.
     * Validation is done as: basic checks on the file (type, size) and check against blocked words.
     * @param file raw data received by request
     * @return CrudResult being "successful" if the file is valid and uploaded
     */
    public CrudResult uploadFile(MultipartFile file) {
        final CrudResult result = new CrudResult();

        if(!validateFile(file, result))
            return result;

        if(fileContainsBannedWords(file))
            return result.setFailure("The supplied file contains blocked content and has been discarded.");

        try {
            Document doc = saveNewDocument(file.getOriginalFilename(), file.getContentType(), file.getSize());
            byte[] fileBytes = file.getBytes();
            Path path = Paths.get(uploadDirectory + doc.getName()); // use filtered file name
            Files.write(path, fileBytes);
            result.setSuccess("The file was successfully uploaded.");
        }catch(IOException e) {
            e.printStackTrace();
            result.setFailure("There was a technical error while uploading the file. Please try again");
        }

        return result;
    }

    /**
     * Performs basic checks on the file (type, size), which are configured in supplied properties
     * @param file the uploaded file
     * @param result the CrudResult to be updated with validation
     * @return true if the file is considered valid, false otherwise
     */
    public boolean validateFile(MultipartFile file, final CrudResult result) {
        if(null == file || file.isEmpty())
            result.setFailure("The supplied file is empty");
        else if(maxFileSize > 0 && file.getSize() > maxFileSize)
            result.setFailure("The supplied file is too large");
        else if(!allowedTypes.isEmpty() && !allowedTypes.contains(file.getContentType()))
            result.setFailure("The type of the supplied file is not supported");
        else result.setSuccess("The file is valid.");

        return result.isSuccess();
    }

    /**
     * Saves metadata of the supplied file in the repository with an additional step to make sure the file name
     * is unique, to avoid overwriting existing files.
     * @param filename filename to be verified and saved
     * @param contentType content type of the file
     * @param size size of the file
     * @return the saved document as object with unique filename according metadata
     */
    public Document saveNewDocument(String filename, String contentType, long size) {
        Document doc = new Document(filename, contentType, size);
        if(documentsRepository.findByName(doc.getName()).isPresent()) {
            // TODO: better handing for existent files .. or better flexibility in file actual file naming and metadata
            String name = doc.getName();
            String suffix = "_" + System.currentTimeMillis();
            int extPosition = name.lastIndexOf(".");
            if(extPosition < 0) doc.setName(name + suffix);
            else doc.setName(name.substring(0, extPosition) + suffix + name.substring(extPosition));
        }

        documentsRepository.save(doc);
        return doc;
    }

    /**
     * Reads the supplied file content and verifies it against the banned words check function. To be called after
     * basic verifications
     * @param file the uploaded file
     * @return true if considered to contain banned words, false otherwise
     */
    public boolean fileContainsBannedWords(MultipartFile file) {
        Set<BannedWord> bannedWords = new HashSet<>();
        for(BannedWord bannedWord : bannedWordsRepository.findAll()) bannedWords.add(bannedWord);
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

        return containsBanned;
    }

    /**
     * Simple word check done on data using the provided banned words. The data is transformed with stripping non-word
     * characters and normalizing to lower case.
     * TODO: use conventional library?
     * @param data a string to be verified
     * @param bannedWords list of banned words to be checked on data
     * @return true of data contains any of the provided banned words, false otherwise
     */
    public boolean dataContainsBannedWord(String data, Set<BannedWord> bannedWords) {
        if(null == data || null == bannedWords) return false; // TODO: exception handling

        data = data.toLowerCase().replaceAll("[^a-zA-Z]", "");
        for(BannedWord bannedWord : bannedWords) {
            if(data.contains(bannedWord.getName())) return true;
        }

        return false;
    }
}
