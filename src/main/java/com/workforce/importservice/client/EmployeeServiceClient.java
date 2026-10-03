package com.workforce.importservice.client;

import com.workforce.importservice.dto.EmployeeBalanceRequest;
import com.workforce.importservice.dto.EmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "employee-service")
public interface EmployeeServiceClient {

    @GetMapping("/api/employees/{id}")
    EmployeeResponse getEmployeeById(@PathVariable("id") Long id);

    @PostMapping("/api/employee-balances/process")
    void processBalance(@RequestBody EmployeeBalanceRequest request);
}
