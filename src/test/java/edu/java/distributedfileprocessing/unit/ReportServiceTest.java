package edu.java.distributedfileprocessing.unit;

import edu.java.distributedfileprocessing.repository.ReportRepository;
import edu.java.distributedfileprocessing.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Spy
    private Clock clock = Clock.fixed(
            LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0)
                    .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC.normalized());

    @InjectMocks
    private ReportService reportService;


    @Test
    public void createReportTest() throws IOException {
        String file = "token tok/token token ";
        InputStream in = new ByteArrayInputStream(file.getBytes());
        Long reportId = 1L;

        reportService.createReport(in, reportId);

        verify(reportRepository).save(argThat(report -> report.getId() == 1L
                && Objects.equals(report.getCreatedAt(), OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                && report.getTokenCount() == 3));
    }

}
