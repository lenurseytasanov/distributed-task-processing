package edu.java.distributedfileprocessing.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Отчет по обработке файла в БД.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Report {

    @Id
    private Long id;

    private OffsetDateTime createdAt;

    private Long wordCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
