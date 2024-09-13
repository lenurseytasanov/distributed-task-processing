package edu.java.distributedfileprocessing.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;

/**
 * Отчет по обработке файла в БД.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Validated
public class Report {

    /**
     * Идентификатор отчета
     */
    @Id
    @NotNull
    private Long id;

    /**
     * Дата создания отчета
     */
    @NotNull
    private OffsetDateTime createdAt;

    /**
     * Количество токенов в текстовом файле. (результат обработки файла)
     */
    @NotNull
    @Min(0)
    private Long tokenCount;

    /**
     * Владелец данных
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
