package edu.java.distributedfileprocessing.unit;

import edu.java.distributedfileprocessing.client.S3Client;
import edu.java.distributedfileprocessing.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private FileService fileService;

    @Test
    public void testSaveFile_Success() {
        InputStream file = mock(InputStream.class);

        String fileId = fileService.saveFile(file);

        verify(s3Client).putObject(eq(fileId), eq(file));
        assertNotNull(fileId);
    }

}
