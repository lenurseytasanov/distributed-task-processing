package edu.java.distributedfileprocessing.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для обработки файлов и получения отчетов.
 */
@RestController
@RequestMapping("/api/v1")
public class ProcessingController {

    @PostMapping("/files")
    public ResponseEntity<?> uploadFile() {
        // TODO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/reports/{report-id}")
    public ResponseEntity<?> getReport(@PathVariable(name = "report-id") String reportId) {
        // TODO
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
