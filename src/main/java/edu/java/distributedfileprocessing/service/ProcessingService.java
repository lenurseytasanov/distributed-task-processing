package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.domain.Report;
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

    /**
     * Сохраняет файл в файловое хранилище и создает задачу на обработку файла.
     */
    @Transactional
    public Long uploadFile(InputStream file) throws IOException {
        String fileId = fileService.saveFile(file);
        Long reportId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        ProcessTask task = new ProcessTask(reportId, fileId);
        rabbitTemplate.convertAndSend("exchange", "process.file", task);
        return reportId;
    }

    /**
     * Загружает файл из файлового хранилища, обрабатывает его и создает отчет.
     * @param id
     */
    @Transactional
    public void processFile(ProcessTask task) throws IOException {
        InputStream file = fileService.getFile(task.getFileId());
        reportService.createReport(file, task.getReportId());
    }

    /**
     * Возвращает сформированный отчет по обработке
     * @param reportId
     * @return
     */
    @Transactional
    public Report getReport(Long reportId) {
        return reportService.getReport(reportId).orElseThrow();
    }

}
