package com.koberp.employeeservice.mapper;

import com.koberp.employeeservice.dto.EmployeeRequest;
import com.koberp.employeeservice.dto.EmployeeResponse;
import com.koberp.employeeservice.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    
    public Employee toEntity(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setBirthDate(request.getBirthDate());
        employee.setHireDate(request.getHireDate());
        employee.setContractEndDate(request.getContractEndDate());
        employee.setPosition(request.getPosition());
        employee.setDepartmentId(request.getDepartmentId());
        employee.setSalary(request.getSalary());
        employee.setPerformanceScore(request.getPerformanceScore());
        employee.setAddress(request.getAddress());
        employee.setEmergencyContactName(request.getEmergencyContactName());
        employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        employee.setEducationLevel(request.getEducationLevel());
        employee.setTotalLeaveDays(request.getTotalLeaveDays());
        employee.setUsedLeaveDays(request.getUsedLeaveDays());
        employee.setRemainingLeaveDays(request.getRemainingLeaveDays());
        employee.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        return employee;
    }
    
    public void updateEntity(Employee employee, EmployeeRequest request) {
        if (request.getEmployeeCode() != null) {
            employee.setEmployeeCode(request.getEmployeeCode());
        }
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setBirthDate(request.getBirthDate());
        employee.setHireDate(request.getHireDate());
        employee.setContractEndDate(request.getContractEndDate());
        employee.setPosition(request.getPosition());
        employee.setDepartmentId(request.getDepartmentId());
        employee.setSalary(request.getSalary());
        employee.setPerformanceScore(request.getPerformanceScore());
        employee.setAddress(request.getAddress());
        employee.setEmergencyContactName(request.getEmergencyContactName());
        employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        employee.setEducationLevel(request.getEducationLevel());
        employee.setTotalLeaveDays(request.getTotalLeaveDays());
        employee.setUsedLeaveDays(request.getUsedLeaveDays());
        employee.setRemainingLeaveDays(request.getRemainingLeaveDays());
        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }
    }
    
    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .birthDate(employee.getBirthDate())
                .hireDate(employee.getHireDate())
                .contractEndDate(employee.getContractEndDate())
                .position(employee.getPosition())
                .departmentId(employee.getDepartmentId())
                .salary(employee.getSalary())
                .performanceScore(employee.getPerformanceScore())
                .age(employee.getAge())
                .yearsOfService(employee.getYearsOfService())
                .address(employee.getAddress())
                .emergencyContactName(employee.getEmergencyContactName())
                .emergencyContactPhone(employee.getEmergencyContactPhone())
                .educationLevel(employee.getEducationLevel())
                .totalLeaveDays(employee.getTotalLeaveDays())
                .usedLeaveDays(employee.getUsedLeaveDays())
                .remainingLeaveDays(employee.getRemainingLeaveDaysCalculated())
                .status(employee.getStatus())
                .avatarPath(employee.getAvatarPath())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
