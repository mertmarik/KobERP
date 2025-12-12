package com.koberp.jobpost.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_posting_id", nullable = false)
    private Integer jobPostingId;

    @Column(name = "applicant_name", length = 200, nullable = false)
    private String applicantName;

    @Column(name = "applicant_email", length = 200, nullable = false)
    private String applicantEmail;

    @Column(name = "applicant_phone", length = 20)
    private String applicantPhone;

    @Column(name = "years_experience")
    private Double yearsExperience;

    @Column(name = "cv_file_path", length = 500)
    private String cvFilePath;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "application_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @Column(name = "status_notes", columnDefinition = "TEXT")
    private String statusNotes;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reviewed_by")
    private Integer reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (applicationStatus == null) {
            applicationStatus = ApplicationStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ApplicationStatus {
        PENDING,   // Bekliyor
        ACCEPTED,  // Kabul edildi
        REJECTED   // Reddedildi
    }
}
