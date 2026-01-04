package com.example.fileproc.application;

import com.example.fileproc.domain.EntryRow;
import com.example.fileproc.domain.OutcomeRow;
import org.springframework.stereotype.Component;

@Component
public class OutcomeTransformer {

    public OutcomeRow transform(EntryRow entryRow) {
        return new OutcomeRow(
                entryRow.name(),
                entryRow.transport(),
                entryRow.topSpeed()
        );
    }
}
