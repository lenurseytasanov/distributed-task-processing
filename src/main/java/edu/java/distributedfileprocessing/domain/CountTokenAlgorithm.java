package edu.java.distributedfileprocessing.domain;

import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

@Component
public class CountTokenAlgorithm implements FileProcessingAlgorithm<Long> {

    @Override
    public Long processFile(@NonNull InputStream file) {
        long tokenCount;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            tokenCount = reader.lines()
                    .mapToLong(line -> new StringTokenizer(line).countTokens())
                    .sum();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tokenCount;
    }

}
