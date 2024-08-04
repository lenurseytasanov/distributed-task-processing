package edu.java.distributedfileprocessing.controller;

import edu.java.distributedfileprocessing.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Контроллер для обработки файлов и получения отчетов.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProcessingController {

    private final ProcessingService processingService;

    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        processingService.uploadFile(inputStream);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/reports/{report-id}")
    public ResponseEntity<?> getReport(@PathVariable(name = "report-id") Long reportId) {
        return new ResponseEntity<>(processingService.getReport(reportId), HttpStatus.OK);
    }

}
