package com.koberp.employeeservice.service;

import com.koberp.employeeservice.dto.EmployeeRequest;
import com.koberp.employeeservice.dto.EmployeeResponse;
import com.koberp.employeeservice.dto.StatsResponse;
import com.koberp.employeeservice.exception.DuplicateResourceException;
import com.koberp.employeeservice.exception.ResourceNotFoundException;
import com.koberp.employeeservice.mapper.EmployeeMapper;
import com.koberp.employeeservice.model.Employee;
import com.koberp.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    
    public List<EmployeeResponse> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public EmployeeResponse getEmployeeById(Long id) {
        log.info("Fetching employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return employeeMapper.toResponse(employee);
    }
    
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating new employee with email: {}", request.getEmail());
        
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee with email " + request.getEmail() + " already exists");
        }
        
        if (request.getEmployeeCode() != null && employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new DuplicateResourceException("Employee with code " + request.getEmployeeCode() + " already exists");
        }
        
        Employee employee = employeeMapper.toEntity(request);
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with id: {}", savedEmployee.getId());
        
        return employeeMapper.toResponse(savedEmployee);
    }
    
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        log.info("Updating employee with id: {}", id);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        if (!employee.getEmail().equals(request.getEmail()) && 
            employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee with email " + request.getEmail() + " already exists");
        }
        
        employeeMapper.updateEntity(employee, request);
        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully with id: {}", updatedEmployee.getId());
        
        return employeeMapper.toResponse(updatedEmployee);
    }
    
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
        log.info("Employee deleted successfully with id: {}", id);
    }
    
    public List<EmployeeResponse> searchEmployees(String query) {
        log.info("Searching employees with query: {}", query);
        return employeeRepository.searchEmployees(query).stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<EmployeeResponse> getEmployeesByDepartment(String departmentId) {
        log.info("Fetching employees by department: {}", departmentId);
        return employeeRepository.findByDepartmentId(departmentId).stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<EmployeeResponse> getEmployeesByPosition(String position) {
        log.info("Fetching employees by position: {}", position);
        return employeeRepository.findByPosition(position).stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<EmployeeResponse> getEmployeesByStatus(String status) {
        log.info("Fetching employees by status: {}", status);
        return employeeRepository.findByStatus(status).stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public StatsResponse getEmployeeStats() {
        log.info("Fetching employee statistics");
        
        Long total = employeeRepository.count();
        Long active = employeeRepository.countByStatus("ACTIVE");
        Long inactive = employeeRepository.countByStatus("INACTIVE");
        Long onLeave = employeeRepository.countByStatus("ON_LEAVE");
        
        Map<String, Long> deptDistribution = new HashMap<>();
        List<Object[]> deptData = employeeRepository.getDepartmentDistribution();
        for (Object[] data : deptData) {
            deptDistribution.put(String.valueOf(data[0]), (Long) data[1]);
        }
        
        Double avgSalaryDouble = employeeRepository.getAverageSalary();
        BigDecimal avgSalary = avgSalaryDouble != null ? 
                BigDecimal.valueOf(avgSalaryDouble).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        List<Employee> allEmployees = employeeRepository.findAll();
        Double avgYearsOfService = allEmployees.stream()
                .filter(e -> e.getHireDate() != null)
                .mapToLong(e -> ChronoUnit.YEARS.between(e.getHireDate(), LocalDate.now()))
                .average()
                .orElse(0.0);
        
        Long newHires = employeeRepository.countNewHiresInMonth(LocalDate.now());
        
        return StatsResponse.builder()
                .totalEmployees(total)
                .activeEmployees(active)
                .inactiveEmployees(inactive)
                .onLeaveEmployees(onLeave)
                .departmentDistribution(deptDistribution)
                .averageSalary(avgSalary)
                .averageYearsOfService(Math.round(avgYearsOfService * 100.0) / 100.0)
                .newHiresThisMonth(newHires)
                .build();
    }
    
    public Long getEmployeeCount() {
        return employeeRepository.count();
    }
    
    public List<String> getAllDepartments() {
        return employeeRepository.findAllDepartmentIds();
    }
    
    public List<String> getAllPositions() {
        return employeeRepository.findAllPositions();
    }
    
    public EmployeeResponse updateEmployeeStatus(Long id, String status) {
        log.info("Updating status for employee id: {} to {}", id, status);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setStatus(status);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }
    
    public EmployeeResponse activateEmployee(Long id) {
        return updateEmployeeStatus(id, "ACTIVE");
    }
    
    public EmployeeResponse deactivateEmployee(Long id) {
        return updateEmployeeStatus(id, "INACTIVE");
    }
    
    public List<EmployeeResponse> createBulkEmployees(List<EmployeeRequest> requests) {
        log.info("Creating bulk employees, count: {}", requests.size());
        return requests.stream()
                .map(this::createEmployee)
                .collect(Collectors.toList());
    }
    
    public void deleteBulkEmployees(List<Long> ids) {
        log.info("Deleting bulk employees, count: {}", ids.size());
        ids.forEach(this::deleteEmployee);
    }
}
