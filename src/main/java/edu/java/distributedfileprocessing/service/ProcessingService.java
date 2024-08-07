package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.config.AppProperties;
import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.exception.NotFoundException;
import edu.java.distributedfileprocessing.queue.ProcessTask;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Сервис верхнего уровня для обработки файла. Используется контроллером
 * {@link edu.java.distributedfileprocessing.controller.ProcessingController} и подписчиком очереди
 * {@link edu.java.distributedfileprocessing.queue.TaskConsumer}
 */
@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final FileService fileService;

    private final ReportService reportService;

    private final RabbitTemplate rabbitTemplate;

    private final AppProperties appProperties;

    /**
     * Сохраняет файл для обработки. Генерирует и возвращает ID для будущего отчета.
     * Создает задачу на обработку файла
     * @param file поток данных для обработки
     * @return ID, по которому можно будет получить отчет после обработки; ID >= 0
     * @throws IOException если ошибка при чтении или записи данных
     */
    @Transactional
    public Long uploadFile(InputStream file) throws IOException {
        String fileId = fileService.saveFile(file);
        Long reportId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        ProcessTask task = new ProcessTask(reportId, fileId);
        rabbitTemplate.convertAndSend(appProperties.getRabbitMq().getExchange(), appProperties.getRabbitMq().getRoutingKey(), task);
        return reportId;
    }

    /**
     * Загружает файл, обрабатывает и формирует отчет.
     * @param task запрос на обработку файла
     * @throws IOException если ошибка при чтении или записи данных
     */
    @Transactional
    public void processFile(ProcessTask task) throws IOException {
        InputStream file = fileService.getFile(task.getFileId());
        reportService.createReport(file, task.getReportId());
    }

    /**
     * Возвращает сформированный отчет по обработке.
     * @param reportId ID отчета
     * @return отчет
     * @throws NotFoundException если отчет с указанным ID еще не создан
     */
    @Transactional
    public Report getReport(Long reportId) throws NotFoundException {
        return reportService.getReport(reportId).orElseThrow(() ->
                new NotFoundException("Report with ID '%d' does not exists".formatted(reportId)));
    }

}
