package com.example.fileproc.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntryFileTokenizerTest {

    private EntryFileTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new EntryFileTokenizer();
    }

    @Test
    void tokenize_withValidInput_returnsCorrectRawLines() throws IOException {
        String content = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5
                1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3
                """;
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        List<RawLine> result = tokenizer.tokenize(inputStream);

        assertEquals(3, result.size());
        assertEquals(1, result.get(0).lineNumber());
        assertEquals(7, result.get(0).tokens().length);
        assertEquals("John Smith", result.get(0).tokens()[2]);
    }

    @Test
    void tokenize_ignoresBlankLines() throws IOException {
        String content = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1

                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5
                """;
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        List<RawLine> result = tokenizer.tokenize(inputStream);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).lineNumber());
        assertEquals(3, result.get(1).lineNumber());
    }

    @Test
    void tokenize_withEmptyFile_returnsEmptyList() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

        List<RawLine> result = tokenizer.tokenize(inputStream);

        assertTrue(result.isEmpty());
    }
}
