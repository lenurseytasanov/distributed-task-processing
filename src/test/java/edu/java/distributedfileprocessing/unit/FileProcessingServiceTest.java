package edu.java.distributedfileprocessing.unit;

import edu.java.distributedfileprocessing.config.AppProperties;
import edu.java.distributedfileprocessing.domain.Report;
import edu.java.distributedfileprocessing.dto.ProcessFileTask;
import edu.java.distributedfileprocessing.exception.NotFoundException;
import edu.java.distributedfileprocessing.exception.NotSupportedAuthenticationException;
import edu.java.distributedfileprocessing.service.FileProcessingService;
import edu.java.distributedfileprocessing.service.FileService;
import edu.java.distributedfileprocessing.service.ReportService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileProcessingServiceTest {

    @Mock
    private FileService fileService;

    @Mock
    private ReportService reportService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AppProperties appProperties;

    @Mock
    private Validator validator;

    @InjectMocks
    private FileProcessingService fileProcessingService;

    @BeforeEach
    void setUp() {
        AppProperties.RabbitMQProperties rabbitMqProperties = new AppProperties.RabbitMQProperties();
        rabbitMqProperties.setExchange("testExchange");
        rabbitMqProperties.setRoutingKey("testRoutingKey");

        lenient().when(appProperties.getRabbitMq()).thenReturn(rabbitMqProperties);
    }

    @Test
    void testUploadFile_Success() {
        // Arrange
        InputStream mockFile = mock(InputStream.class);
        OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class, RETURNS_DEEP_STUBS);
        when(authentication.getPrincipal().getAttribute("email")).thenReturn("test@example.com");

        // Mock file service behavior
        when(fileService.saveFile(mockFile)).thenReturn("fileId123");

        // No constraint violations for ProcessFileTask
        when(validator.validate(any(ProcessFileTask.class))).thenReturn(Collections.emptySet());

        // Call the method
        Long reportId = fileProcessingService.uploadFile(mockFile, authentication);

        // Verify the expected behavior
        verify(fileService).saveFile(mockFile);
        verify(rabbitTemplate).convertAndSend(eq("testExchange"), eq("testRoutingKey"), any(ProcessFileTask.class));
        assertNotNull(reportId);
    }

    @Test
    void testUploadFile_InvalidAuthenticationType() {
        InputStream mockFile = mock(InputStream.class);
        Authentication authentication = mock(Authentication.class);  // Not OAuth2AuthenticationToken

        NotSupportedAuthenticationException exception = assertThrows(
                NotSupportedAuthenticationException.class,
                () -> fileProcessingService.uploadFile(mockFile, authentication)
        );

        assertEquals("Not support authentication type [%s]".formatted(authentication), exception.getMessage());
    }

    @Test
    void testUploadFile_UserWithoutEmail() {
        InputStream mockFile = mock(InputStream.class);
        OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class, RETURNS_DEEP_STUBS);

        // Simulate missing email in the OAuth2 authentication
        when(authentication.getPrincipal().getAttribute("email")).thenReturn(null);

        NotSupportedAuthenticationException exception = assertThrows(
                NotSupportedAuthenticationException.class,
                () -> fileProcessingService.uploadFile(mockFile, authentication)
        );

        assertEquals("User must have email", exception.getMessage());
    }

    @Test
    void testUploadFile_WithConstraintViolation() {
        InputStream mockFile = mock(InputStream.class);
        OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class, RETURNS_DEEP_STUBS);
        when(authentication.getPrincipal().getAttribute("email")).thenReturn("test@example.com");

        // Mock file service behavior
        when(fileService.saveFile(mockFile)).thenReturn("fileId123");

        // Simulate constraint violations
        ConstraintViolation<ProcessFileTask> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Invalid task");
        when(validator.validate(any(ProcessFileTask.class))).thenReturn(Set.of(violation));

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> fileProcessingService.uploadFile(mockFile, authentication)
        );

        assertNotNull(exception.getConstraintViolations());
        assertEquals(1, exception.getConstraintViolations().size());
    }

    @Test
    void testProcessFile_Success() throws IOException {
        // Mock task
        ProcessFileTask task = new ProcessFileTask(123L, "fileId123", "test@example.com");

        // Mock file retrieval from file service
        InputStream mockFile = mock(InputStream.class);
        when(fileService.getFile("fileId123")).thenReturn(() -> mockFile);

        // Call the method
        fileProcessingService.processFile(task);

        // Verify that report creation was triggered
        verify(reportService).createReport(mockFile, 123L, "test@example.com");
    }

    @Test
    void testGetReport_Success() throws NotFoundException {
        // Mock report retrieval
        Report mockReport = new Report();
        when(reportService.getReport(123L)).thenReturn(Optional.of(mockReport));

        // Call the method
        Report report = fileProcessingService.getReport(123L);

        // Verify behavior
        verify(reportService).getReport(123L);
        assertEquals(mockReport, report);
    }

    @Test
    void testGetReport_NotFound() {
        // Simulate report not found
        when(reportService.getReport(123L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> fileProcessingService.getReport(123L));

        assertEquals("Report with ID '123' does not exists", exception.getMessage());
    }
}

