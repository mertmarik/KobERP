package com.koberp.employeeservice.repository;

import com.koberp.employeeservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmployeeCode(String employeeCode);
    
    Optional<Employee> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmployeeCode(String employeeCode);
    
    List<Employee> findByDepartmentId(String departmentId);
    
    List<Employee> findByPosition(String position);
    
    List<Employee> findByStatus(String status);
    
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.position) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Employee> searchEmployees(@Param("query") String query);
    
    @Query("SELECT DISTINCT e.departmentId FROM Employee e ORDER BY e.departmentId")
    List<String> findAllDepartmentIds();
    
    @Query("SELECT DISTINCT e.position FROM Employee e ORDER BY e.position")
    List<String> findAllPositions();
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status")
    Long countByStatus(@Param("status") String status);
    
    @Query("SELECT e.departmentId, COUNT(e) FROM Employee e GROUP BY e.departmentId")
    List<Object[]> getDepartmentDistribution();
    
    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.salary IS NOT NULL")
    Double getAverageSalary();
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE " +
           "YEAR(e.hireDate) = YEAR(:date) AND MONTH(e.hireDate) = MONTH(:date)")
    Long countNewHiresInMonth(@Param("date") LocalDate date);
}
