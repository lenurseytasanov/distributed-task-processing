package edu.java.distributedfileprocessing.unit;

import edu.java.distributedfileprocessing.domain.FileProcessingAlgorithm;
import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.domain.User;
import edu.java.distributedfileprocessing.repository.ReportRepository;
import edu.java.distributedfileprocessing.repository.UserRepository;
import edu.java.distributedfileprocessing.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileProcessingAlgorithm<Long> fileProcessingAlgorithm;

    private final ZonedDateTime mockTime = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("Z"));

    @Spy
    private Clock clock = Clock.fixed(mockTime.toInstant(), mockTime.getZone());

    @InjectMocks
    private ReportService reportService;

    @Test
    void testGetReport_Success() {
        // Mock a report ID and a saved report
        Long reportId = 123L;
        Report mockReport = new Report();
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(mockReport));

        // Call the method
        Optional<Report> result = reportService.getReport(reportId);

        // Verify the behavior and the result
        assertTrue(result.isPresent());
        assertEquals(mockReport, result.get());
        verify(reportRepository).findById(reportId);
    }

    @Test
    void testGetReport_NotFound() {
        // Mock a report ID with no report found
        Long reportId = 123L;
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // Call the method
        Optional<Report> result = reportService.getReport(reportId);

        // Verify the behavior and the result
        assertFalse(result.isPresent());
        verify(reportRepository).findById(reportId);
    }

    @Test
    void testCreateReport_Success() {
        // Mock file, report ID, and user email
        InputStream mockFile = mock(InputStream.class);
        Long reportId = 123L;
        String userEmail = "test@example.com";

        // Mock user and file processing behavior
        User mockUser = new User();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(fileProcessingAlgorithm.processFile(mockFile)).thenReturn(1000L);

        // Call the method
        reportService.createReport(mockFile, reportId, userEmail);

        // Verify that a report is created and saved
        verify(fileProcessingAlgorithm).processFile(mockFile);
        verify(userRepository).findByEmail(userEmail);
        verify(reportRepository).save(any(Report.class));

        // Capture the created report for further verification
        verify(reportRepository).save(argThat(report ->
                report.getId().equals(reportId) &&
                        report.getTokenCount() == 1000L &&
                        report.getUser().equals(mockUser) &&
                        report.getCreatedAt().equals(mockTime.toOffsetDateTime())
        ));
    }

    @Test
    void testCreateReport_UserNotFound() {
        // Mock file, report ID, and user email
        InputStream mockFile = mock(InputStream.class);
        Long reportId = 123L;
        String userEmail = "test@example.com";

        // Simulate user not found
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
        when(fileProcessingAlgorithm.processFile(any())).thenReturn(1L);

        // Call the method and expect an exception
        assertThrows(NoSuchElementException.class, () -> reportService.createReport(mockFile, reportId, userEmail));

        // Verify that no report is saved when user is not found
        verify(reportRepository, never()).save(any(Report.class));
    }
}
