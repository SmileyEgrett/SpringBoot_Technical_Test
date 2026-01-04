package com.example.fileproc.application;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnProperty(name = "features.fileValidation.enabled", havingValue = "false")
public class NoopEntryFileValidator implements EntryFileValidator {

    @Override
    public List<String> validate(List<RawLine> rawLines) {
        return Collections.emptyList();
    }
}
