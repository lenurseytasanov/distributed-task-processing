package edu.java.distributedfileprocessing.domain;

import lombok.NonNull;

import java.io.InputStream;

public interface FileProcessingAlgorithm<T> {

    T processFile(@NonNull InputStream file);

}
