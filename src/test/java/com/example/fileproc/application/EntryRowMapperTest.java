package com.example.fileproc.application;

import com.example.fileproc.domain.EntryRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EntryRowMapperTest {

    private EntryRowMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EntryRowMapper();
    }

    @Test
    void map_withValidInput_returnsEntryRow() {
        String uuid = "18148426-89e1-11ee-b9d1-0242ac120002";
        String[] tokens = {uuid, "1X1D14", "John Smith", "Likes Apricots", "Rides A Bike", "6.2", "12.1"};
        RawLine rawLine = new RawLine(1, tokens);

        EntryRow result = mapper.map(rawLine);

        assertEquals(UUID.fromString(uuid), result.uuid());
        assertEquals("1X1D14", result.id());
        assertEquals("John Smith", result.name());
        assertEquals("Likes Apricots", result.likes());
        assertEquals("Rides A Bike", result.transport());
        assertEquals(new BigDecimal("6.2"), result.avgSpeed());
        assertEquals(new BigDecimal("12.1"), result.topSpeed());
    }

    @Test
    void map_withWhitespaceInTokens_trimsThem() {
        String uuid = "18148426-89e1-11ee-b9d1-0242ac120002";
        String[] tokens = {"  " + uuid + "  ", " 1X1D14 ", " John Smith ", " Likes ", " Bike ", " 6.2 ", " 12.1 "};
        RawLine rawLine = new RawLine(1, tokens);

        EntryRow result = mapper.map(rawLine);

        assertEquals(UUID.fromString(uuid), result.uuid());
        assertEquals("1X1D14", result.id());
        assertEquals("John Smith", result.name());
    }

    @Test
    void map_withInvalidUuid_throwsFileParseException() {
        String[] tokens = {"not-a-uuid", "1X1D14", "John Smith", "Likes", "Bike", "6.2", "12.1"};
        RawLine rawLine = new RawLine(5, tokens);

        FileParseException exception = assertThrows(
                FileParseException.class,
                () -> mapper.map(rawLine)
        );

        assertEquals(5, exception.getLineNumber());
        assertTrue(exception.getMessage().contains("line 5"));
        assertTrue(exception.getMessage().contains("invalid data format"));
    }

    @Test
    void map_withInvalidAvgSpeed_throwsFileParseException() {
        String uuid = "18148426-89e1-11ee-b9d1-0242ac120002";
        String[] tokens = {uuid, "1X1D14", "John Smith", "Likes", "Bike", "not-a-number", "12.1"};
        RawLine rawLine = new RawLine(3, tokens);

        FileParseException exception = assertThrows(
                FileParseException.class,
                () -> mapper.map(rawLine)
        );

        assertEquals(3, exception.getLineNumber());
        assertTrue(exception.getMessage().contains("invalid data format"));
    }

    @Test
    void map_withInvalidTopSpeed_throwsFileParseException() {
        String uuid = "18148426-89e1-11ee-b9d1-0242ac120002";
        String[] tokens = {uuid, "1X1D14", "John Smith", "Likes", "Bike", "6.2", "invalid"};
        RawLine rawLine = new RawLine(2, tokens);

        FileParseException exception = assertThrows(
                FileParseException.class,
                () -> mapper.map(rawLine)
        );

        assertEquals(2, exception.getLineNumber());
    }

    @Test
    void map_withInsufficientColumns_throwsFileParseException() {
        String uuid = "18148426-89e1-11ee-b9d1-0242ac120002";
        String[] tokens = {uuid, "id", "name", "likes", "transport"};
        RawLine rawLine = new RawLine(1, tokens);

        FileParseException exception = assertThrows(
                FileParseException.class,
                () -> mapper.map(rawLine)
        );

        assertTrue(exception.getMessage().contains("insufficient columns"));
    }
}
