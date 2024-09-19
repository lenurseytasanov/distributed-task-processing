package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.domain.FileProcessingAlgorithm;
import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.domain.User;
import edu.java.distributedfileprocessing.repository.ReportRepository;
import edu.java.distributedfileprocessing.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Сервис для работы с отчетами по обработке.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;

    private final UserRepository userRepository;

    private final FileProcessingAlgorithm<Long> fileProcessingAlgorithm;

    private final Clock clock;

    /**
     * Возвращает сохраненный отчет.
     * @param id ID отчета
     * @return отчет, если существует, иначе Optional.empty().
     */
    public Optional<Report> getReport(@NonNull Long id) {
        return reportRepository.findById(id);
    }

    /**
     * Обрабатывает файл и создает отчет. Отчет сохраняется в базу по указанному ID
     * @param file файл для обработки
     * @param reportId ID отчета
     */
    @Transactional
    public void createReport(@NonNull InputStream file, @NonNull Long reportId, @NonNull String userEmail) {
        OffsetDateTime createdAt = OffsetDateTime.now(clock);
        long tokenCount = fileProcessingAlgorithm.processFile(file);

        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Report report = new Report(reportId, createdAt, tokenCount, user);

        reportRepository.save(report);
        log.info("save report '{}'", reportId);
    }

}
