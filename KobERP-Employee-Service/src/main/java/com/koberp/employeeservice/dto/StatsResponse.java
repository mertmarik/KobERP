package com.koberp.employeeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    
    private Long totalEmployees;
    private Long activeEmployees;
    private Long inactiveEmployees;
    private Long onLeaveEmployees;
    private Map<String, Long> departmentDistribution;
    private BigDecimal averageSalary;
    private Double averageYearsOfService;
    private Long newHiresThisMonth;
}
