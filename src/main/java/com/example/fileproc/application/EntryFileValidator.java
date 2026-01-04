package com.example.fileproc.application;

import java.util.List;

public interface EntryFileValidator {

    List<String> validate(List<RawLine> rawLines);
}
