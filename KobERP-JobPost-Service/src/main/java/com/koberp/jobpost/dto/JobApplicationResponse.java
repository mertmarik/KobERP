package com.koberp.jobpost.dto;

import com.koberp.jobpost.entity.JobApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponse {

    private Integer id;
    private Integer jobPostingId;
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private Double yearsExperience;
    private String cvFilePath;
    private String coverLetter;
    private JobApplication.ApplicationStatus applicationStatus;
    private String statusNotes;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private Integer reviewedBy;
    private LocalDateTime reviewedAt;

    public JobApplicationResponse(JobApplication application) {
        this.id = application.getId();
        this.jobPostingId = application.getJobPostingId();
        this.applicantName = application.getApplicantName();
        this.applicantEmail = application.getApplicantEmail();
        this.applicantPhone = application.getApplicantPhone();
        this.yearsExperience = application.getYearsExperience();
        this.cvFilePath = application.getCvFilePath();
        this.coverLetter = application.getCoverLetter();
        this.applicationStatus = application.getApplicationStatus();
        this.statusNotes = application.getStatusNotes();
        this.appliedAt = application.getAppliedAt();
        this.updatedAt = application.getUpdatedAt();
        this.reviewedBy = application.getReviewedBy();
        this.reviewedAt = application.getReviewedAt();
    }
}
