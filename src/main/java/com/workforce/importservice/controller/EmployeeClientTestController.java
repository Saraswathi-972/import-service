package com.workforce.importservice.controller;

import com.workforce.importservice.client.EmployeeServiceClient;
import com.workforce.importservice.dto.EmployeeResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class EmployeeClientTestController {

    private final EmployeeServiceClient employeeServiceClient;

    public EmployeeClientTestController(EmployeeServiceClient employeeServiceClient) {
        this.employeeServiceClient = employeeServiceClient;
    }

    @GetMapping("/employees/{id}")
    public EmployeeResponse getEmployee(@PathVariable Long id) {
        return employeeServiceClient.getEmployeeById(id);
    }
}