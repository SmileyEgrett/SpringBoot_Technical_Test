package com.example.fileproc.domain;

public record BlockingResult(
        boolean blocked,
        String reason
) {
    public static BlockingResult allow() {
        return new BlockingResult(false, null);
    }

    public static BlockingResult deny(String reason) {
        return new BlockingResult(true, reason);
    }
}
