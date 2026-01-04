package com.example.fileproc.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record EntryRow(
        UUID uuid,
        String id,
        String name,
        String likes,
        String transport,
        BigDecimal avgSpeed,
        BigDecimal topSpeed
) {
}
