package com.example.fileproc.application;

import com.example.fileproc.domain.OutcomeRow;

public interface OutcomeRowExtractor {
    OutcomeRow extract(RawLine rawLine);
}
