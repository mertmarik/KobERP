package com.koberp.jobpost.service;

import com.koberp.jobpost.dto.JobApplicationRequest;
import com.koberp.jobpost.dto.JobApplicationResponse;
import com.koberp.jobpost.entity.JobApplication;
import com.koberp.jobpost.entity.JobPost;
import com.koberp.jobpost.repository.JobApplicationRepository;
import com.koberp.jobpost.repository.JobPostRepository;
import com.koberp.jobpost.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;
    private final AuthenticationUtil authenticationUtil;

    @Transactional
    public JobApplicationResponse createApplication(JobApplicationRequest request) {
        JobApplication application = new JobApplication();
        application.setJobPostingId(request.getJobPostingId());
        application.setApplicantName(request.getApplicantName());
        application.setApplicantEmail(request.getApplicantEmail());
        application.setApplicantPhone(request.getApplicantPhone());
        application.setYearsExperience(request.getYearsExperience());
        application.setCvFilePath(request.getCvFilePath());
        application.setCoverLetter(request.getCoverLetter());
        
        if (request.getApplicationStatus() != null) {
            application.setApplicationStatus(request.getApplicationStatus());
        } else {
            application.setApplicationStatus(JobApplication.ApplicationStatus.PENDING);
        }

        JobApplication savedApplication = jobApplicationRepository.save(application);
        return new JobApplicationResponse(savedApplication);
    }

    public JobApplicationResponse getApplicationById(Integer id) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Başvuru bulunamadı: " + id));
        return new JobApplicationResponse(application);
    }

    public List<JobApplicationResponse> getAllApplications() {
        return jobApplicationRepository.findAll().stream()
                .map(JobApplicationResponse::new)
                .collect(Collectors.toList());
    }

    public List<JobApplicationResponse> getApplicationsByJobPostId(Integer jobPostingId) {
        // İş ilanının sahibi kontrolü
        JobPost jobPost = jobPostRepository.findById(jobPostingId)
                .orElseThrow(() -> new RuntimeException("İş ilanı bulunamadı: " + jobPostingId));
        
        String currentUserId = authenticationUtil.getCurrentUserId();
        if (!jobPost.getOwnerId().equals(currentUserId)) {
            throw new RuntimeException("Bu iş ilanının başvurularını görüntüleme yetkiniz yok");
        }
        
        return jobApplicationRepository.findByJobPostingId(jobPostingId).stream()
                .map(JobApplicationResponse::new)
                .collect(Collectors.toList());
    }

    public List<JobApplicationResponse> getApplicationsByStatus(JobApplication.ApplicationStatus status) {
        return jobApplicationRepository.findByApplicationStatus(status).stream()
                .map(JobApplicationResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobApplicationResponse acceptApplication(Integer id, Integer reviewedBy, String statusNotes) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Başvuru bulunamadı: " + id));

        // İş ilanının sahibi kontrolü
        validateJobPostOwnership(application.getJobPostingId());

        application.setApplicationStatus(JobApplication.ApplicationStatus.ACCEPTED);
        application.setStatusNotes(statusNotes);
        application.setReviewedBy(reviewedBy);
        application.setReviewedAt(LocalDateTime.now());

        JobApplication updatedApplication = jobApplicationRepository.save(application);
        return new JobApplicationResponse(updatedApplication);
    }

    @Transactional
    public JobApplicationResponse rejectApplication(Integer id, Integer reviewedBy, String statusNotes) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Başvuru bulunamadı: " + id));

        // İş ilanının sahibi kontrolü
        validateJobPostOwnership(application.getJobPostingId());

        application.setApplicationStatus(JobApplication.ApplicationStatus.REJECTED);
        application.setStatusNotes(statusNotes);
        application.setReviewedBy(reviewedBy);
        application.setReviewedAt(LocalDateTime.now());

        JobApplication updatedApplication = jobApplicationRepository.save(application);
        return new JobApplicationResponse(updatedApplication);
    }

    @Transactional
    public JobApplicationResponse updateApplicationStatus(
            Integer id, 
            JobApplication.ApplicationStatus status,
            Integer reviewedBy,
            String statusNotes
    ) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Başvuru bulunamadı: " + id));

        // İş ilanının sahibi kontrolü
        validateJobPostOwnership(application.getJobPostingId());

        application.setApplicationStatus(status);
        application.setStatusNotes(statusNotes);
        application.setReviewedBy(reviewedBy);
        application.setReviewedAt(LocalDateTime.now());

        JobApplication updatedApplication = jobApplicationRepository.save(application);
        return new JobApplicationResponse(updatedApplication);
    }

    @Transactional
    public void deleteApplication(Integer id) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Başvuru bulunamadı: " + id));
        
        // İş ilanının sahibi kontrolü
        validateJobPostOwnership(application.getJobPostingId());
        
        jobApplicationRepository.deleteById(id);
    }

    /**
     * İş ilanının sahibi olup olmadığını kontrol eder
     */
    private void validateJobPostOwnership(Integer jobPostingId) {
        JobPost jobPost = jobPostRepository.findById(jobPostingId)
                .orElseThrow(() -> new RuntimeException("İş ilanı bulunamadı: " + jobPostingId));
        
        String currentUserId = authenticationUtil.getCurrentUserId();
        if (!jobPost.getOwnerId().equals(currentUserId)) {
            throw new RuntimeException("Bu işlemi gerçekleştirme yetkiniz yok");
        }
    }
}
