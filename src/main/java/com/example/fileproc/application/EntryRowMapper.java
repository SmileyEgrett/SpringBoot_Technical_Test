package com.example.fileproc.application;

import com.example.fileproc.domain.EntryRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class EntryRowMapper {

    public EntryRow map(RawLine rawLine) {
        int lineNum = rawLine.lineNumber();
        String[] tokens = rawLine.tokens();

        try {
            return new EntryRow(
                    UUID.fromString(tokens[0].trim()),
                    tokens[1].trim(),
                    tokens[2].trim(),
                    tokens[3].trim(),
                    tokens[4].trim(),
                    new BigDecimal(tokens[5].trim()),
                    new BigDecimal(tokens[6].trim())
            );
        } catch (IllegalArgumentException e) {
            throw new FileParseException(lineNum, "invalid data format: " + e.getMessage(), e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new FileParseException(lineNum, "insufficient columns", e);
        }
    }
}
