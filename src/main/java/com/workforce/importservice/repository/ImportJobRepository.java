package com.workforce.importservice.repository;

import com.workforce.importservice.entity.ImportJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportJobRepository extends JpaRepository<ImportJob, Long> {
}
