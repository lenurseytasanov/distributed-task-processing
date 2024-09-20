package edu.java.distributedfileprocessing.client;

import lombok.NonNull;

import java.io.InputStream;
import java.util.function.Supplier;

public interface S3Client {

    Supplier<InputStream> getObject(@NonNull String objectName);

    void putObject(@NonNull String objectName, @NonNull InputStream object);

}
