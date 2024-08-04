package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.domain.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

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

    /**
     * Сохраняет файл в файловое хранилище и создает задачу на обработку файла.
     */
    public void uploadFile() {
        fileService.saveFile();
        rabbitTemplate.convertAndSend(null);
        // TODO
    }

    /**
     * Загружает файл из файлового хранилища, обрабатывает его и создает отчет.
     * @param id
     */
    public void processFile(Long id) {
        fileService.getFile(id);
        reportService.createReport();
        // TODO
    }

    /**
     * Возвращает сформированный отчет по обработке
     * @param reportId
     * @return
     */
    public Report getReport(Long reportId) {
        return reportService.getReport(reportId);
    }

}
