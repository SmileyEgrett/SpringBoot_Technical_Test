package com.example.fileproc.application;

import java.util.List;

public class FileValidationException extends RuntimeException {

    private final List<String> errors;

    public FileValidationException(List<String> errors) {
        super("File validation failed: " + errors.size() + " error(s)");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
