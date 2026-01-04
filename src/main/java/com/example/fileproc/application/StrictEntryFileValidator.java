package com.example.fileproc.application;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "features.fileValidation.enabled", havingValue = "true", matchIfMissing = true)
public class StrictEntryFileValidator implements EntryFileValidator {

    private static final int EXPECTED_COLUMNS = 7;

    @Override
    public List<String> validate(List<RawLine> rawLines) {
        List<String> errors = new ArrayList<>();

        if (rawLines.isEmpty()) {
            errors.add("File is empty");
            return errors;
        }

        for (RawLine rawLine : rawLines) {
            validateLine(rawLine, errors);
        }

        return errors;
    }

    private void validateLine(RawLine rawLine, List<String> errors) {
        int lineNum = rawLine.lineNumber();
        String[] tokens = rawLine.tokens();

        if (tokens.length != EXPECTED_COLUMNS) {
            errors.add("line " + lineNum + ": expected " + EXPECTED_COLUMNS + " columns but got " + tokens.length);
            return;
        }

        // Validate UUID
        try {
            UUID.fromString(tokens[0].trim());
        } catch (IllegalArgumentException e) {
            errors.add("line " + lineNum + ": invalid UUID");
        }

        // Validate Name not blank
        if (tokens[2].trim().isEmpty()) {
            errors.add("line " + lineNum + ": Name cannot be blank");
        }

        // Validate Transport not blank
        if (tokens[4].trim().isEmpty()) {
            errors.add("line " + lineNum + ": Transport cannot be blank");
        }

        // Validate AvgSpeed
        BigDecimal avgSpeed = null;
        try {
            avgSpeed = new BigDecimal(tokens[5].trim());
            if (avgSpeed.compareTo(BigDecimal.ZERO) < 0) {
                errors.add("line " + lineNum + ": AvgSpeed must be >= 0");
            }
        } catch (NumberFormatException e) {
            errors.add("line " + lineNum + ": invalid AvgSpeed");
        }

        // Validate TopSpeed
        BigDecimal topSpeed = null;
        try {
            topSpeed = new BigDecimal(tokens[6].trim());
            if (topSpeed.compareTo(BigDecimal.ZERO) < 0) {
                errors.add("line " + lineNum + ": TopSpeed must be >= 0");
            }
        } catch (NumberFormatException e) {
            errors.add("line " + lineNum + ": invalid TopSpeed");
        }

        // Validate TopSpeed >= AvgSpeed
        if (avgSpeed != null && topSpeed != null && topSpeed.compareTo(avgSpeed) < 0) {
            errors.add("line " + lineNum + ": TopSpeed must be >= AvgSpeed");
        }
    }
}
