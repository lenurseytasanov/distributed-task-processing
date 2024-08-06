package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * Сервис для работы с отчетами по обработке.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    private final Clock clock;

    /**
     * Возвращает сохраненный отчет.
     * @param id
     * @return
     */
    public Optional<Report> getReport(Long id) {
        return reportRepository.findById(id);
    }

    /**
     * Формирует и сохраняет отчет в БД.
     */
    public void createReport(InputStream file, Long reportId) throws IOException {
        OffsetDateTime createdAt = OffsetDateTime.now(clock);
        long tokenCount;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            tokenCount = reader.lines()
                    .mapToLong(line -> new StringTokenizer(line).countTokens())
                    .sum();
        }
        Report report = new Report(reportId, createdAt, tokenCount, null);
        reportRepository.save(report);
    }

}
