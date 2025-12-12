package com.koberp.employeeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private LocalDate contractEndDate;
    private String position;
    private String departmentId;
    private BigDecimal salary;
    private BigDecimal performanceScore;
    private Integer age;
    private Integer yearsOfService;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String educationLevel;
    private Integer totalLeaveDays;
    private Integer usedLeaveDays;
    private Integer remainingLeaveDays;
    private String status;
    private String avatarPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
