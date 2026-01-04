package com.example.fileproc.application;

import com.example.fileproc.domain.OutcomeRow;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "features.fileValidation.enabled", havingValue = "true", matchIfMissing = true)
public class StrictOutcomeRowExtractor implements OutcomeRowExtractor {

    private final EntryRowMapper mapper;
    private final OutcomeTransformer transformer;

    public StrictOutcomeRowExtractor(EntryRowMapper mapper, OutcomeTransformer transformer) {
        this.mapper = mapper;
        this.transformer = transformer;
    }

    @Override
    public OutcomeRow extract(RawLine rawLine) {
        return transformer.transform(mapper.map(rawLine));
    }
}
