package com.example.fileproc.application;

import com.example.fileproc.domain.OutcomeRow;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class FileProcessingUseCase {

    private final EntryFileTokenizer tokenizer;
    private final EntryFileValidator validator;
    private final OutcomeRowExtractor extractor;

    public FileProcessingUseCase(EntryFileTokenizer tokenizer,
                                  EntryFileValidator validator,
                                  OutcomeRowExtractor extractor) {
        this.tokenizer = tokenizer;
        this.validator = validator;
        this.extractor = extractor;
    }

    public List<OutcomeRow> process(InputStream inputStream) throws IOException {
        List<RawLine> rawLines = tokenizer.tokenize(inputStream);

        List<String> errors = validator.validate(rawLines);
        if (!errors.isEmpty()) {
            throw new FileValidationException(errors);
        }

        return rawLines.stream()
                .map(extractor::extract)
                .toList();
    }
}
