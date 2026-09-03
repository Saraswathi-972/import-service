package com.workforce.importservice.service;

import com.workforce.importservice.dto.BalanceImportRecord;

import java.io.InputStream;
import java.util.List;

public interface CsvParserService {
    List<BalanceImportRecord> parseCsv(InputStream inputStream);
}
