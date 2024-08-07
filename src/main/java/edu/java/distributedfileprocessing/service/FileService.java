package edu.java.distributedfileprocessing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Сервис для работы с загруженными файлами.
 */
@Service
@RequiredArgsConstructor
public class FileService {

    /**
     * Сохраняет файл в хранилище.
     */
    public String saveFile(InputStream file) throws IOException {
        String fileId = UUID.randomUUID().toString();
        Path target = Paths.get("temp", fileId);
        try (file) {
            Files.copy(file, target);
        }
        return fileId;
    }

    /**
     * Получает файл из хранилища.
     * @param id
     * @return
     */
    public InputStream getFile(String id) throws IOException {
        Path path = Paths.get("temp", id);
        return Files.newInputStream(path);
    }

}
