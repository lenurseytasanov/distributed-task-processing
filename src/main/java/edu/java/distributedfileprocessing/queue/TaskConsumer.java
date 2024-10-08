package edu.java.distributedfileprocessing.queue;

import edu.java.distributedfileprocessing.dto.ProcessFileTask;
import edu.java.distributedfileprocessing.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Подписан на очередь с задачами на обработку файла.
 */
@Component
@RequiredArgsConstructor
public class TaskConsumer {

    private final FileProcessingService fileProcessingService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "process.file", durable = "true"),
            exchange = @Exchange(value = "exchange", ignoreDeclarationExceptions = "true"),
            key = "process.file")
    )
    public void processTask(ProcessFileTask task) throws IOException {
        fileProcessingService.processFile(task);
    }
}
