package edu.java.distributedfileprocessing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private RabbitMQProperties rabbitMq;

    @Data
    public static class RabbitMQProperties {

        private String exchange;

        private String routingKey;

        private String queue;

    }
}
