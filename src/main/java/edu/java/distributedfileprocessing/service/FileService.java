package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.client.S3Client;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Сервис для работы с загруженными файлами.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final S3Client s3Client;

    /**
     * Сохраняет файл в S3 хранилище.
     * @param file потока файла
     * @return ID файла в базе
     */
    public String saveFile(@NonNull InputStream file) {
        String fileId = UUID.randomUUID().toString();
        s3Client.putObject(fileId, file);
        log.info("Save file '{}'", fileId);
        return fileId;
    }

    /**
     * Получает файл из хранилища.
     * @param id ID файла
     * @return предоставляет новый поток файла
     */
    public Supplier<InputStream> getFile(@NonNull String id) {
        return s3Client.getObject(id);
    }

}
