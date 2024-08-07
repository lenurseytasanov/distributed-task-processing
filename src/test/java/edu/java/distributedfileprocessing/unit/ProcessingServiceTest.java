package edu.java.distributedfileprocessing.unit;

import edu.java.distributedfileprocessing.config.AppProperties;
import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.exception.NotFoundException;
import edu.java.distributedfileprocessing.queue.ProcessTask;
import edu.java.distributedfileprocessing.service.FileService;
import edu.java.distributedfileprocessing.service.ProcessingService;
import edu.java.distributedfileprocessing.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProcessingServiceTest {

    @Mock
    private FileService fileService;

    @Mock
    private ReportService reportService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AppProperties appProperties;

    @InjectMocks
    private ProcessingService processingService;

    /**
     * Ожидается:
     * <ul>
     *     <li>Файл сохранет</li>
     *     <li>ID возвращен и не отрицателен</li>
     *     <li>Сообщение в RabbitMQ отправлено</li>
     * </ul>
     */
    @Test
    public void uploadFileTest() throws IOException {
        when(appProperties.getRabbitMq().getExchange()).thenReturn("exchange");
        when(appProperties.getRabbitMq().getRoutingKey()).thenReturn("process.file");

        try (InputStream in = InputStream.nullInputStream()) {
            Long reportId = processingService.uploadFile(in);

            verify(fileService).saveFile(eq(in));

            assertNotNull(reportId);
            assertTrue(reportId >= 0);

            verify(rabbitTemplate).convertAndSend(eq("exchange"), eq("process.file"),
                    Optional.ofNullable(argThat(message -> message instanceof ProcessTask
                            && Objects.equals(((ProcessTask) message).getReportId(), reportId))));
        }
    }

    /**
     * Ожидается:
     * <ul>
     *     <li>Создается отчет по файлу</li>
     * </ul>
     */
    @Test
    public void processFile() throws IOException {
        ProcessTask task = new ProcessTask(1L, "1");

        try (InputStream in = InputStream.nullInputStream()) {
            when(fileService.getFile(any())).thenReturn(in);

            processingService.processFile(task);

            verify(reportService).createReport(eq(in), eq(task.getReportId()));
        }
    }

    /**
     * Ожидается:
     * <ul>
     *     <li>Возаращается отчет, если найден</li>
     *     <li>Исключение, если отчет не найден</li>
     * </ul>
     */
    @Test
    public void getReport() {
        Report report = new Report(1L, null, 1L, null);

        when(reportService.getReport(any())).thenReturn(Optional.of(report));
        Report actual = processingService.getReport(1L);
        assertEquals(report, actual);

        when(reportService.getReport(any())).thenReturn(Optional.empty());
        Exception ex = assertThrows(NotFoundException.class, () -> processingService.getReport(1L));
        assertEquals("Report with ID '1' does not exists", ex.getMessage());
    }

}
