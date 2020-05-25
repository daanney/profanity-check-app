package com.sforce.profanitycheck.document;

import javax.persistence.*;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String type;
    private Long size;

    public Document() {}

    public Document(String name) {
        this.name = name;
    }

    public Document(String name, String type, Long size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public Document(Long id, String name, String type, Long size) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
