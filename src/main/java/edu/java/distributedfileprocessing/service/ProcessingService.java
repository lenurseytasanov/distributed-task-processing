package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.config.AppProperties;
import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.exception.NotFoundException;
import edu.java.distributedfileprocessing.exception.NotSupportedAuthenticationException;
import edu.java.distributedfileprocessing.queue.ProcessTask;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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
@Slf4j
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
     */
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Long uploadFile(@NonNull InputStream file) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof OidcUser oidcUserPrincipal)) {
            throw new NotSupportedAuthenticationException("Can't handle authentication %s".formatted(principal));
        }

        String principalEmail = oidcUserPrincipal.getEmail();
        String fileId = fileService.saveFile(file);
        Long reportId = Math.abs(UUID.randomUUID().getLeastSignificantBits());

        ProcessTask task = new ProcessTask(reportId, fileId, principalEmail);
        rabbitTemplate.convertAndSend(appProperties.getRabbitMq().getExchange(), appProperties.getRabbitMq().getRoutingKey(), task);
        return reportId;
    }

    /**
     * Загружает файл, обрабатывает и формирует отчет.
     * @param task запрос на обработку файла
     * @throws IOException если ошибка при чтении или записи данных
     */
    @Transactional
    public void processFile(@NonNull ProcessTask task) throws IOException {
        try (InputStream file = fileService.getFile(task.getFileId()).get()) {
            reportService.createReport(file, task.getReportId(), task.getUserEmail());
        }
    }

    /**
     * Возвращает сформированный отчет по обработке.
     * @param reportId ID отчета
     * @return отчет
     * @throws NotFoundException если отчет с указанным ID еще не создан
     */
    @Transactional
    public Report getReport(@NonNull Long reportId) throws NotFoundException {
        return reportService.getReport(reportId).orElseThrow(() ->
                new NotFoundException("Report with ID '%d' does not exists".formatted(reportId)));
    }

}
