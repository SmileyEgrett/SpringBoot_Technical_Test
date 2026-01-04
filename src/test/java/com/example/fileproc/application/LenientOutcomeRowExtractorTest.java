package com.example.fileproc.application;

import com.example.fileproc.domain.OutcomeRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LenientOutcomeRowExtractorTest {

    private LenientOutcomeRowExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new LenientOutcomeRowExtractor();
    }

    @Test
    void extract_withValidInput_returnsOutcomeRow() {
        String[] tokens = {"any-uuid", "any-id", "John Smith", "Likes Apricots", "Rides A Bike", "any-speed", "12.1"};
        RawLine rawLine = new RawLine(1, tokens);

        OutcomeRow result = extractor.extract(rawLine);

        assertEquals("John Smith", result.name());
        assertEquals("Rides A Bike", result.transport());
        assertEquals(new BigDecimal("12.1"), result.topSpeed());
    }

    @Test
    void extract_withInvalidUuidAndAvgSpeed_stillSucceeds() {
        String[] tokens = {"not-a-uuid", "id", "Jane Doe", "Likes", "Drives a Car", "not-a-number", "55.5"};
        RawLine rawLine = new RawLine(1, tokens);

        OutcomeRow result = extractor.extract(rawLine);

        assertEquals("Jane Doe", result.name());
        assertEquals("Drives a Car", result.transport());
        assertEquals(new BigDecimal("55.5"), result.topSpeed());
    }

    @Test
    void extract_withWhitespaceInTokens_trimsThem() {
        String[] tokens = {"uuid", "id", "  John Smith  ", "likes", "  Bike  ", "speed", "  12.1  "};
        RawLine rawLine = new RawLine(1, tokens);

        OutcomeRow result = extractor.extract(rawLine);

        assertEquals("John Smith", result.name());
        assertEquals("Bike", result.transport());
        assertEquals(new BigDecimal("12.1"), result.topSpeed());
    }

    @Test
    void extract_withInsufficientColumns_throwsFileParseException() {
        String[] tokens = {"uuid", "id", "name", "likes", "transport", "speed"};
        RawLine rawLine = new RawLine(3, tokens);

        FileParseException exception = assertThrows(
                FileParseException.class,
                () -> extractor.extract(rawLine)
        );

        assertEquals(3, exception.getLineNumber());
        assertTrue(exception.getMessage().contains("insufficient columns"));
    }

    @Test
    void extract_withEmptyName_throwsFileParseException() {
        String[] tokens = {"uuid", "id", "", "likes", "Bike", "speed", "12.1"};
        RawLine rawLine = new RawLine(2, tokens);

        FileParseException exception = assertThrows(
                FileParseException.class,
                () -> extractor.extract(rawLine)
        );

        assertEquals(2, exception.getLineNumber());
        assertTrue(exception.getMessage().contains("Name is required"));
    }

    @Test
    void extract_withBlankName_throwsFileParseException() {
        String[] tokens = {"uuid", "id", "   ", "likes", "Bike", "speed", "12.1"};
        RawLine rawLine = new RawLine(2, tokens);

        FileParseException exception = assertThrows(
                FileParseException.class,
                () -> extractor.extract(rawLine)
        );

        assertTrue(exception.getMessage().contains("Name is required"));
    }

    @Test
    void extract_withEmptyTransport_throwsFileParseException() {
        String[] tokens = {"uuid", "id", "John", "likes", "", "speed", "12.1"};
        RawLine rawLine = new RawLine(4, tokens);

        FileParseException exception = assertThrows(
                FileParseException.class,
                () -> extractor.extract(rawLine)
        );

        assertEquals(4, exception.getLineNumber());
        assertTrue(exception.getMessage().contains("Transport is required"));
    }

    @Test
    void extract_withInvalidTopSpeed_throwsFileParseException() {
        String[] tokens = {"uuid", "id", "John", "likes", "Bike", "speed", "not-a-number"};
        RawLine rawLine = new RawLine(5, tokens);

        FileParseException exception = assertThrows(
                FileParseException.class,
                () -> extractor.extract(rawLine)
        );

        assertEquals(5, exception.getLineNumber());
        assertTrue(exception.getMessage().contains("invalid TopSpeed"));
    }
}
