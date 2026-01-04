package com.example.fileproc.application;

public class FileParseException extends RuntimeException {

    private final int lineNumber;

    public FileParseException(int lineNumber, String message) {
        super("line " + lineNumber + ": " + message);
        this.lineNumber = lineNumber;
    }

    public FileParseException(int lineNumber, String message, Throwable cause) {
        super("line " + lineNumber + ": " + message, cause);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
