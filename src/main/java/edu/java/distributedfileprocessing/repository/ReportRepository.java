package edu.java.distributedfileprocessing.repository;

import edu.java.distributedfileprocessing.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для отчетов.
 */
public interface ReportRepository extends JpaRepository<Report, Long> {
}
