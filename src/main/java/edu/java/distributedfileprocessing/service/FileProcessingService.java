package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.config.AppProperties;
import edu.java.distributedfileprocessing.controller.FileProcessingController;
import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.dto.ProcessFileTask;
import edu.java.distributedfileprocessing.exception.NotFoundException;
import edu.java.distributedfileprocessing.exception.NotSupportedAuthenticationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис верхнего уровня для обработки файла. Используется контроллером
 * {@link FileProcessingController} и подписчиком очереди
 * {@link edu.java.distributedfileprocessing.queue.TaskConsumer}
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class FileProcessingService {

    private final FileService fileService;

    private final ReportService reportService;

    private final RabbitTemplate rabbitTemplate;

    private final AppProperties appProperties;

    private final Validator validator;

    /**
     * Сохраняет файл для обработки. Генерирует и возвращает ID для будущего отчета.
     * Создает задачу на обработку файла
     * @param file поток данных для обработки
     * @return ID, по которому можно будет получить отчет после обработки; ID >= 0
     */
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Long uploadFile(@NonNull InputStream file, @NonNull Authentication authentication) {
        String userEmail;
        if (!(authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken)) {
            throw new NotSupportedAuthenticationException("Not support authentication type [%s]".formatted(authentication));
        } else if ((userEmail = oAuth2AuthenticationToken.getPrincipal().getAttribute("email")) == null) {
            throw new NotSupportedAuthenticationException("User must have email");
        }

        String fileId = fileService.saveFile(file);
        ProcessFileTask task = createProcessFileTask(fileId, userEmail);
        rabbitTemplate.convertAndSend(appProperties.getRabbitMq().getExchange(), appProperties.getRabbitMq().getRoutingKey(), task);
        return task.getReportId();
    }

    private ProcessFileTask createProcessFileTask(String fileId, String userEmail) {
        Long reportId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        ProcessFileTask task = new ProcessFileTask(reportId, fileId, userEmail);
        Set<ConstraintViolation<ProcessFileTask>> violations = validator.validate(task);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return task;
    }

    /**
     * Загружает файл, обрабатывает и формирует отчет.
     * @param task запрос на обработку файла
     * @throws IOException если ошибка при чтении или записи данных
     */
    @Transactional
    public void processFile(@NonNull ProcessFileTask task) throws IOException {
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
    @PreAuthorize("isAuthenticated()")
    @PostAuthorize("returnObject.user.sub == authentication.name")
    public Report getReport(@NonNull @Min(0) Long reportId) throws NotFoundException {
        return reportService.getReport(reportId).orElseThrow(() ->
                new NotFoundException("Report with ID '%d' does not exists".formatted(reportId)));
    }

}
