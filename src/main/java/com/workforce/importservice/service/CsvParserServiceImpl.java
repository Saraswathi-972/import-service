package com.workforce.importservice.service;

import com.workforce.importservice.dto.BalanceImportRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserServiceImpl implements CsvParserService {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    public List<BalanceImportRecord> parseCsv(InputStream inputStream) {
        List<BalanceImportRecord> records = new ArrayList<>();
        try(Reader reader=new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .build();
            try(CSVParser csvParser = csvFormat.parse(reader)){
                for(CSVRecord csvRecord: csvParser){
                    BalanceImportRecord record = new BalanceImportRecord();
                    record.setAaid(csvRecord.get("AAID"));
                    record.setAction(csvRecord.get("Action"));
                    record.setBalanceName(csvRecord.get("BalanceName"));
                    record.setBalanceValue(new BigDecimal(csvRecord.get("BalanceValue")));
                    record.setEffectiveDate(
                            LocalDate.parse(
                                    csvRecord.get("EffectiveDate"),
                                    DATE_FORMATTER));
                    record.setComments(csvRecord.get("Comments"));
                    records.add(record);
                }
            }
            return records;
        }catch(Exception e){
            throw new RuntimeException("Failed to parse CSV", e);
        }

    }
}
