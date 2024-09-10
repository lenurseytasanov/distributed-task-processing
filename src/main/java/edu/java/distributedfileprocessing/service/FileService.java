package edu.java.distributedfileprocessing.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Сервис для работы с загруженными файлами.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    /**
     * Сохраняет файл в хранилище.
     */
    public String saveFile(@NonNull InputStream file) {
        String fileId = UUID.randomUUID().toString();
        Path target = Paths.get("temp", fileId);
        try {
            Files.copy(file, target);
        } catch (IOException e) {
            log.error("I/O error occurs when saving file '%s'".formatted(fileId), e);
            throw new RuntimeException(e);
        }
        return fileId;
    }

    /**
     * Получает файл из хранилища.
     * @param id
     * @return
     */
    public Supplier<InputStream> getFile(@NonNull String id) {
        Path path = Paths.get("temp", id);
        return () -> {
            try {
                return Files.newInputStream(path);
            } catch (IOException e) {
                log.error("I/O error occurs when reading file", e);
                throw new RuntimeException(e);
            }
        };
    }

}
