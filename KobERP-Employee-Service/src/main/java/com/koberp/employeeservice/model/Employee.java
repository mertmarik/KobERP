package com.koberp.employeeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE employees SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_id", unique = true, nullable = false, length = 50)
    private String employeeId;
    
    @Column(name = "employee_code", unique = true, length = 50)
    private String employeeCode;
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;
    
    @Column(nullable = false, length = 150)
    private String position;
    
    @Column(name = "department", nullable = false, length = 100)
    private String departmentId;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal salary;
    
    @Column(name = "performance_score", precision = 3, scale = 2)
    private BigDecimal performanceScore;
    
    @Column(columnDefinition = "text")
    private String address;
    
    @Column(name = "emergency_contact_name", length = 150)
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;
    
    @Column(name = "education_level", length = 100)
    private String educationLevel;
    
    @Column(name = "total_leave_days")
    private Integer totalLeaveDays;
    
    @Column(name = "used_leave_days")
    private Integer usedLeaveDays;
    
    @Column(name = "remaining_leave_days", insertable = false, updatable = false)
    private Integer remainingLeaveDays;
    
    @Column(name = "status", length = 50)
    private String status = "ACTIVE";
    
    @Column(name = "avatar_path", length = 255)
    private String avatarPath;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (employeeId == null || employeeId.isEmpty()) {
            employeeId = generateEmployeeId();
        }
        if (employeeCode == null || employeeCode.isEmpty()) {
            employeeCode = generateEmployeeCode();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateEmployeeId() {
        return "EID" + System.currentTimeMillis();
    }
    
    private String generateEmployeeCode() {
        return "EMP" + System.currentTimeMillis();
    }
    
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Transient
    public Integer getAge() {
        if (birthDate != null) {
            return LocalDate.now().getYear() - birthDate.getYear();
        }
        return null;
    }
    
    @Transient
    public Integer getYearsOfService() {
        if (hireDate != null) {
            return LocalDate.now().getYear() - hireDate.getYear();
        }
        return null;
    }
    
    @Transient
    public Integer getRemainingLeaveDaysCalculated() {
        if (totalLeaveDays != null && usedLeaveDays != null) {
            return totalLeaveDays - usedLeaveDays;
        }
        return remainingLeaveDays;
    }
}
