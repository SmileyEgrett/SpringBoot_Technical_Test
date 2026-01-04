package com.example.fileproc.application;

import com.example.fileproc.domain.OutcomeRow;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConditionalOnProperty(name = "features.fileValidation.enabled", havingValue = "false")
public class LenientOutcomeRowExtractor implements OutcomeRowExtractor {

    @Override
    public OutcomeRow extract(RawLine rawLine) {
        String[] tokens = rawLine.tokens();
        int lineNum = rawLine.lineNumber();

        if (tokens.length < 7) {
            throw new FileParseException(lineNum, "insufficient columns");
        }

        String name = tokens[2].trim();
        String transport = tokens[4].trim();

        if (name.isEmpty()) {
            throw new FileParseException(lineNum, "Name is required");
        }
        if (transport.isEmpty()) {
            throw new FileParseException(lineNum, "Transport is required");
        }

        try {
            BigDecimal topSpeed = new BigDecimal(tokens[6].trim());
            return new OutcomeRow(name, transport, topSpeed);
        } catch (NumberFormatException e) {
            throw new FileParseException(lineNum, "invalid TopSpeed", e);
        }
    }
}
