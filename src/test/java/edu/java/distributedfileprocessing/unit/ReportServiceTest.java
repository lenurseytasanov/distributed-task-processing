package edu.java.distributedfileprocessing.unit;

import edu.java.distributedfileprocessing.repository.ReportRepository;
import edu.java.distributedfileprocessing.service.ReportService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;


}
