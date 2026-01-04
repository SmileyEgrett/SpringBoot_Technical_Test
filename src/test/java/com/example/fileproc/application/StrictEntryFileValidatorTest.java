package com.example.fileproc.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StrictEntryFileValidatorTest {

    private StrictEntryFileValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StrictEntryFileValidator();
    }

    @Test
    void validate_withValidInput_returnsNoErrors() {
        List<RawLine> rawLines = List.of(
                new RawLine(1, new String[]{
                        "18148426-89e1-11ee-b9d1-0242ac120002", "1X1D14", "John Smith",
                        "Likes Apricots", "Rides A Bike", "6.2", "12.1"
                })
        );

        List<String> errors = validator.validate(rawLines);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validate_withWrongColumnCount_returnsError() {
        List<RawLine> rawLines = List.of(
                new RawLine(2, new String[]{"uuid", "id", "name", "likes", "transport", "speed"})
        );

        List<String> errors = validator.validate(rawLines);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("line 2"));
        assertTrue(errors.get(0).contains("expected 7 columns but got 6"));
    }

    @Test
    void validate_withInvalidUuid_returnsError() {
        List<RawLine> rawLines = List.of(
                new RawLine(1, new String[]{
                        "not-a-uuid", "1X1D14", "John Smith",
                        "Likes Apricots", "Rides A Bike", "6.2", "12.1"
                })
        );

        List<String> errors = validator.validate(rawLines);

        assertTrue(errors.stream().anyMatch(e -> e.contains("invalid UUID")));
    }

    @Test
    void validate_withBlankName_returnsError() {
        List<RawLine> rawLines = List.of(
                new RawLine(3, new String[]{
                        "18148426-89e1-11ee-b9d1-0242ac120002", "1X1D14", "",
                        "Likes Apricots", "Rides A Bike", "6.2", "12.1"
                })
        );

        List<String> errors = validator.validate(rawLines);

        assertTrue(errors.stream().anyMatch(e -> e.contains("Name cannot be blank")));
    }

    @Test
    void validate_withInvalidSpeed_returnsError() {
        List<RawLine> rawLines = List.of(
                new RawLine(1, new String[]{
                        "18148426-89e1-11ee-b9d1-0242ac120002", "1X1D14", "John Smith",
                        "Likes Apricots", "Rides A Bike", "not-a-number", "12.1"
                })
        );

        List<String> errors = validator.validate(rawLines);

        assertTrue(errors.stream().anyMatch(e -> e.contains("invalid AvgSpeed")));
    }

    @Test
    void validate_withTopSpeedLessThanAvgSpeed_returnsError() {
        List<RawLine> rawLines = List.of(
                new RawLine(1, new String[]{
                        "18148426-89e1-11ee-b9d1-0242ac120002", "1X1D14", "John Smith",
                        "Likes Apricots", "Rides A Bike", "50.0", "30.0"
                })
        );

        List<String> errors = validator.validate(rawLines);

        assertTrue(errors.stream().anyMatch(e -> e.contains("TopSpeed must be >= AvgSpeed")));
    }

    @Test
    void validate_withEmptyFile_returnsError() {
        List<RawLine> rawLines = List.of();

        List<String> errors = validator.validate(rawLines);

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("File is empty"));
    }
}
