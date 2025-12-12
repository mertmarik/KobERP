package com.koberp.employeeservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {
    
    private String employeeCode;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate birthDate;
    
    private LocalDate hireDate;
    
    private LocalDate contractEndDate;
    
    @NotBlank(message = "Position is required")
    @Size(max = 150)
    private String position;
    
    @NotBlank(message = "Department is required")
    @Size(max = 100)
    private String departmentId;
    
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;
    
    @DecimalMin(value = "0.0", message = "Performance score must be at least 0")
    @DecimalMax(value = "5.0", message = "Performance score cannot exceed 5")
    private BigDecimal performanceScore;
    
    private String address;
    
    @Size(max = 150)
    private String emergencyContactName;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid emergency contact phone")
    private String emergencyContactPhone;
    
    @Size(max = 100)
    private String educationLevel;
    
    @Min(value = 0, message = "Total leave days cannot be negative")
    private Integer totalLeaveDays;
    
    @Min(value = 0, message = "Used leave days cannot be negative")
    private Integer usedLeaveDays;
    
    @Min(value = 0, message = "Remaining leave days cannot be negative")
    private Integer remainingLeaveDays;
    
    @Size(max = 50)
    private String status;
}
