package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с отчетами по обработке.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    /**
     * Возвращает сохраненный отчет.
     * @param id
     * @return
     */
    public Report getReport(Long id) {
        reportRepository.findById(id);
        return null;
    }

    /**
     * Формирует и сохраняет отчет в БД.
     */
    public void createReport() {
        // TODO
    }

}
