package com.example.fileproc.domain;

import java.math.BigDecimal;

public record OutcomeRow(
        String name,
        String transport,
        BigDecimal topSpeed
) {
}
