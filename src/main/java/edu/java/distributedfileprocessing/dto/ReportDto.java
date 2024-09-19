package edu.java.distributedfileprocessing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto {

    private Long id;

    private OffsetDateTime createdAt;

    private Long tokenCount;

    private String userEmail;
}
