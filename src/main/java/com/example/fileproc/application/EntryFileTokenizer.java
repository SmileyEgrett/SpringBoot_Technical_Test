package com.example.fileproc.application;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class EntryFileTokenizer {

    public List<RawLine> tokenize(InputStream inputStream) throws IOException {
        List<RawLine> rawLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();

                if (trimmed.isEmpty()) {
                    continue;
                }

                String[] tokens = trimmed.split("\\|", -1);
                for (int i = 0; i < tokens.length; i++) {
                    tokens[i] = tokens[i].trim();
                }
                rawLines.add(new RawLine(lineNumber, tokens));
            }
        }

        return rawLines;
    }
}
