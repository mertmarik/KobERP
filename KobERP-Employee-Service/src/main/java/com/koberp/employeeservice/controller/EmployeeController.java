package com.koberp.employeeservice.controller;

import com.koberp.employeeservice.dto.EmployeeRequest;
import com.koberp.employeeservice.dto.EmployeeResponse;
import com.koberp.employeeservice.dto.StatsResponse;
import com.koberp.employeeservice.dto.StatusUpdateRequest;
import com.koberp.employeeservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Employee Management", description = "APIs for managing employee records")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @GetMapping
    @Operation(summary = "Get all employees", description = "Retrieve a list of all employees in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved employees",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Retrieve a specific employee by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved employee",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponse.class))),
        @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @Parameter(description = "Employee ID", required = true, example = "1") 
            @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create new employee", description = "Create a new employee record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Employee created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "409", description = "Employee already exists", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        log.info("Received create employee request: {}", request);
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update employee", description = "Update an existing employee record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponse.class))),
        @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee", description = "Soft delete an employee (marks as deleted)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search employees", description = "Search employees by name, email, department, or position")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<List<EmployeeResponse>> searchEmployees(
            @Parameter(description = "Search query", required = true, example = "john")
            @RequestParam String q) {
        return ResponseEntity.ok(employeeService.searchEmployees(q));
    }
    
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get employees by department", description = "Retrieve all employees in a specific department")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(
            @Parameter(description = "Department ID", required = true, example = "Engineering")
            @PathVariable String departmentId) {
        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(departmentId));
    }
    
    @GetMapping("/position/{position}")
    @Operation(summary = "Get employees by position", description = "Retrieve all employees with a specific position")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByPosition(
            @Parameter(description = "Position title", required = true, example = "Software Engineer")
            @PathVariable String position) {
        return ResponseEntity.ok(employeeService.getEmployeesByPosition(position));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get employees by status", description = "Retrieve all employees with a specific status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByStatus(
            @Parameter(description = "Employee status", required = true, example = "ACTIVE")
            @PathVariable String status) {
        return ResponseEntity.ok(employeeService.getEmployeesByStatus(status));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get employee statistics", description = "Retrieve aggregated statistics about employees")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatsResponse.class)))
    public ResponseEntity<StatsResponse> getEmployeeStats() {
        return ResponseEntity.ok(employeeService.getEmployeeStats());
    }
    
    @GetMapping("/count")
    @Operation(summary = "Get employee count", description = "Get the total number of active employees")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    public ResponseEntity<Long> getEmployeeCount() {
        return ResponseEntity.ok(employeeService.getEmployeeCount());
    }
    
    @GetMapping("/departments")
    @Operation(summary = "Get all departments", description = "Retrieve a list of all unique departments")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved departments")
    public ResponseEntity<List<String>> getAllDepartments() {
        return ResponseEntity.ok(employeeService.getAllDepartments());
    }
    
    @GetMapping("/positions")
    @Operation(summary = "Get all positions", description = "Retrieve a list of all unique positions")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved positions")
    public ResponseEntity<List<String>> getAllPositions() {
        return ResponseEntity.ok(employeeService.getAllPositions());
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update employee status", description = "Update the status of an employee")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    public ResponseEntity<EmployeeResponse> updateEmployeeStatus(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployeeStatus(id, request.getStatus()));
    }
    
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate employee", description = "Activate an inactive employee")
    @ApiResponse(responseCode = "200", description = "Employee activated successfully")
    public ResponseEntity<EmployeeResponse> activateEmployee(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.activateEmployee(id));
    }
    
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate employee", description = "Deactivate an active employee")
    @ApiResponse(responseCode = "200", description = "Employee deactivated successfully")
    public ResponseEntity<EmployeeResponse> deactivateEmployee(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.deactivateEmployee(id));
    }
    
    @PostMapping("/bulk")
    @Operation(summary = "Create bulk employees", description = "Create multiple employee records at once")
    @ApiResponse(responseCode = "201", description = "Employees created successfully")
    public ResponseEntity<List<EmployeeResponse>> createBulkEmployees(
            @Valid @RequestBody List<EmployeeRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createBulkEmployees(requests));
    }
    
    @DeleteMapping("/bulk")
    @Operation(summary = "Delete bulk employees", description = "Delete multiple employees at once (soft delete)")
    @ApiResponse(responseCode = "204", description = "Employees deleted successfully")
    public ResponseEntity<Void> deleteBulkEmployees(@RequestBody List<Long> ids) {
        employeeService.deleteBulkEmployees(ids);
        return ResponseEntity.noContent().build();
    }
}
