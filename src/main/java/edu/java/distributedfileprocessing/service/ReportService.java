package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.domain.User;
import edu.java.distributedfileprocessing.repository.ReportRepository;
import edu.java.distributedfileprocessing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * Сервис для работы с отчетами по обработке.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    private final UserRepository userRepository;

    private final Clock clock;

    /**
     * Возвращает сохраненный отчет.
     * @param id ID отчета
     * @return отчет, если существует, иначе Optional.empty().
     */
    public Optional<Report> getReport(Long id) {
        Objects.requireNonNull(id);
        return reportRepository.findById(id);
    }

    /**
     * Обрабатывает файл и создает отчет. Отчет сохраняется в базу по указанному ID
     * @param file файл для обработки
     * @param reportId ID отчета
     * @throws IOException если ошибка при чтении или записи данных
     */
    @Transactional
    public void createReport(InputStream file, Long reportId, String userEmail) throws IOException {
        OffsetDateTime createdAt = OffsetDateTime.now(clock);
        long tokenCount;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            tokenCount = reader.lines()
                    .mapToLong(line -> new StringTokenizer(line).countTokens())
                    .sum();
        }

        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Report report = new Report(reportId, createdAt, tokenCount, user);

        reportRepository.save(report);
    }

}
