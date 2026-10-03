package com.workforce.importservice.repository;

import com.workforce.importservice.entity.BalanceImportRecordEntity;
import com.workforce.importservice.entity.ImportRecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface BalanceImportRecordRepository extends JpaRepository<BalanceImportRecordEntity, Long> {
    List<BalanceImportRecordEntity> findByImportJobIdAndImportRecordStatus(
            Long importId,
            ImportRecordStatus status
    );
    Page<BalanceImportRecordEntity> findByImportJobIdAndImportRecordStatus(
            Long importId,
            ImportRecordStatus status,
            Pageable pageable
    );
}
