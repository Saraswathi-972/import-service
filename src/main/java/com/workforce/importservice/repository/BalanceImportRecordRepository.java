package com.workforce.importservice.repository;

import com.workforce.importservice.entity.BalanceImportRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceImportRecordRepository extends JpaRepository<BalanceImportRecordEntity, Long> {
}
