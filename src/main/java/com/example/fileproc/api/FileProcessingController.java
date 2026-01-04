package com.example.fileproc.api;

import com.example.fileproc.application.FileProcessingUseCase;
import com.example.fileproc.domain.OutcomeRow;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileProcessingController {

    private final FileProcessingUseCase fileProcessingUseCase;

    public FileProcessingController(FileProcessingUseCase fileProcessingUseCase) {
        this.fileProcessingUseCase = fileProcessingUseCase;
    }

    @PostMapping(value = "/outcome-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<OutcomeRow>> processFile(@RequestParam("file") MultipartFile file) throws IOException {
        List<OutcomeRow> outcomes = fileProcessingUseCase.process(file.getInputStream());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"OutcomeFile.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(outcomes);
    }
}
