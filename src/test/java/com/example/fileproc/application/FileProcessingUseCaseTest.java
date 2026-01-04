package com.example.fileproc.application;

import com.example.fileproc.domain.OutcomeRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileProcessingUseCaseTest {

    private EntryFileTokenizer tokenizer;
    private EntryFileValidator validator;
    private OutcomeRowExtractor extractor;
    private FileProcessingUseCase useCase;

    @BeforeEach
    void setUp() {
        tokenizer = mock(EntryFileTokenizer.class);
        validator = mock(EntryFileValidator.class);
        extractor = mock(OutcomeRowExtractor.class);
        useCase = new FileProcessingUseCase(tokenizer, validator, extractor);
    }

    @Test
    void process_withValidInput_returnsOutcomeRows() throws IOException {
        InputStream input = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));
        RawLine rawLine1 = new RawLine(1, new String[]{"uuid", "id", "John", "likes", "Bike", "10", "20"});
        RawLine rawLine2 = new RawLine(2, new String[]{"uuid2", "id2", "Jane", "likes", "Car", "50", "100"});
        List<RawLine> rawLines = List.of(rawLine1, rawLine2);

        when(tokenizer.tokenize(any(InputStream.class))).thenReturn(rawLines);
        when(validator.validate(rawLines)).thenReturn(List.of());
        when(extractor.extract(rawLine1)).thenReturn(new OutcomeRow("John", "Bike", new BigDecimal("20")));
        when(extractor.extract(rawLine2)).thenReturn(new OutcomeRow("Jane", "Car", new BigDecimal("100")));

        List<OutcomeRow> result = useCase.process(input);

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).name());
        assertEquals("Jane", result.get(1).name());
        verify(tokenizer).tokenize(any(InputStream.class));
        verify(validator).validate(rawLines);
        verify(extractor, times(2)).extract(any(RawLine.class));
    }

    @Test
    void process_withValidationErrors_throwsFileValidationException() throws IOException {
        InputStream input = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));
        RawLine rawLine = new RawLine(1, new String[]{"invalid"});
        List<RawLine> rawLines = List.of(rawLine);
        List<String> errors = List.of("line 1: invalid UUID", "line 1: insufficient columns");

        when(tokenizer.tokenize(any(InputStream.class))).thenReturn(rawLines);
        when(validator.validate(rawLines)).thenReturn(errors);

        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> useCase.process(input)
        );

        assertEquals(2, exception.getErrors().size());
        verify(extractor, never()).extract(any(RawLine.class));
    }

    @Test
    void process_withEmptyFile_returnsEmptyList() throws IOException {
        InputStream input = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        List<RawLine> rawLines = List.of();

        when(tokenizer.tokenize(any(InputStream.class))).thenReturn(rawLines);
        when(validator.validate(rawLines)).thenReturn(List.of());

        List<OutcomeRow> result = useCase.process(input);

        assertTrue(result.isEmpty());
        verify(extractor, never()).extract(any(RawLine.class));
    }

    @Test
    void process_callsComponentsInCorrectOrder() throws IOException {
        InputStream input = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));
        RawLine rawLine = new RawLine(1, new String[]{"uuid", "id", "John", "likes", "Bike", "10", "20"});
        List<RawLine> rawLines = List.of(rawLine);

        when(tokenizer.tokenize(any(InputStream.class))).thenReturn(rawLines);
        when(validator.validate(rawLines)).thenReturn(List.of());
        when(extractor.extract(rawLine)).thenReturn(new OutcomeRow("John", "Bike", new BigDecimal("20")));

        useCase.process(input);

        var inOrder = inOrder(tokenizer, validator, extractor);
        inOrder.verify(tokenizer).tokenize(any(InputStream.class));
        inOrder.verify(validator).validate(rawLines);
        inOrder.verify(extractor).extract(rawLine);
    }
}
