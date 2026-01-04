package com.example.fileproc.application;

public record RawLine(
        int lineNumber,
        String[] tokens
) {
}
