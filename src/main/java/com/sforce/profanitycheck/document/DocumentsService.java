package com.sforce.profanitycheck.document;

import com.sforce.profanitycheck.common.ApiResult;
import com.sforce.profanitycheck.validation.*;
import com.sforce.profanitycheck.wordlist.BannedWord;
import com.sforce.profanitycheck.wordlist.BannedWordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

@Service
public class DocumentsService {

    @Value("${profanitycheck.upload.dir}")
    private String uploadDirectory;

    @Autowired
    private BannedWordsRepository bannedWordsRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private Validations validations;

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
     * @param result api result containing the success and message of the operation
     * @return true if the file is valid and uploaded, false otherwise.
     */
    public boolean uploadFile(MultipartFile file, ApiResult result) {
        List<BannedWord> bannedWords = new LinkedList<>();
        for(BannedWord bannedWord : bannedWordsRepository.findAll()) bannedWords.add(bannedWord);

        if(!validations.validatesAll(file, result, bannedWords))
            return false;

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
}
