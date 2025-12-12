package com.koberp.jobpost.repository;

import com.koberp.jobpost.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {
    
    List<JobApplication> findByJobPostingId(Integer jobPostingId);
    
    List<JobApplication> findByApplicationStatus(JobApplication.ApplicationStatus applicationStatus);
    
    List<JobApplication> findByJobPostingIdAndApplicationStatus(
        Integer jobPostingId, 
        JobApplication.ApplicationStatus applicationStatus
    );
    
    long countByJobPostingId(Integer jobPostingId);
}
