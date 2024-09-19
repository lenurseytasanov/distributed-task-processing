package edu.java.distributedfileprocessing.controller;

import edu.java.distributedfileprocessing.dto.ReportDto;
import edu.java.distributedfileprocessing.mapper.ReportMapper;
import edu.java.distributedfileprocessing.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
public class FileProcessingController {

    private final FileProcessingService fileProcessingService;

    private final ReportMapper reportMapper;

    /**
     * Загружает файл для обработки
     * @param file
     * @return идентификатор для получения отчета после обработки
     * @throws IOException
     */
    @PostMapping("/files")
    public ResponseEntity<Long> uploadFile(@RequestParam("file") MultipartFile file, Authentication authentication) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            Long reportId = fileProcessingService.uploadFile(inputStream, authentication);
            return new ResponseEntity<>(reportId, HttpStatus.OK);
        }
    }

    /**
     * Возвращает результат обработки файла
     * @param reportId
     * @return
     */
    @GetMapping("/reports/{report-id}")
    public ResponseEntity<ReportDto> getReport(@PathVariable(name = "report-id") Long reportId) {
        ReportDto reportDto = reportMapper.toDto(fileProcessingService.getReport(reportId));
        return new ResponseEntity<>(reportDto, HttpStatus.OK);
    }

}
