package com.sforce.profanitycheck.document;

import com.sforce.profanitycheck.common.CrudResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("documents")
public class DocumentsController {

    @Autowired
    private DocumentsService documentsService;

    @GetMapping
    public ResponseEntity<Iterable<Document>> getDocuments() {
        Iterable<Document> docs = documentsService.getDocuments();
        return new ResponseEntity<>(docs, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Document> getDocument(@RequestParam("id") Long id) {
        Document doc = documentsService.getDocument(id);
        if(null == doc) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(doc, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CrudResult> uploadDocument(@RequestParam("file") MultipartFile file) {
        CrudResult result = documentsService.uploadFile(file);

        if(result.isSuccess()) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
