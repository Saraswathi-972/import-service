package com.workforce.importservice.service;

import com.workforce.importservice.dto.BalanceImportRecord;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BalanceImportValidator {

    public String validateBalanceRecord(BalanceImportRecord record){
        if(StringUtils.isBlank(record.getAaid())){
            return "AAID is required";
        }
        if(!record.getAaid().matches("\\d+")){
            return "AAID must be a number";
        }
        if(StringUtils.isBlank(record.getBalanceName())){
            return "Balance Name is required";
        }
        if(StringUtils.isBlank(record.getBalanceValue().toString())){
            return "Balance Value is required";
        }
        if(record.getBalanceValue().signum()<0){
            return "Balance Value must be a positive number";
        }
        if(StringUtils.isBlank(record.getAction())){
            return "Action is required";
        }
        if(!(record.getAction().equalsIgnoreCase("ADD") || record.getAction().equalsIgnoreCase("SET"))){
            return "Action must be either ADD or SET";
        }
        if(StringUtils.isBlank(record.getEffectiveDate().toString())){
            return "Effective Date is required";
        }
        return null;
    }
}
