package com.koberp.jobpost.service;

import com.koberp.jobpost.dto.JobPostRequest;
import com.koberp.jobpost.dto.JobPostResponse;
import com.koberp.jobpost.entity.JobPost;
import com.koberp.jobpost.repository.JobApplicationRepository;
import com.koberp.jobpost.repository.JobPostRepository;
import com.koberp.jobpost.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostService {

    private final JobPostRepository jobPostRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final AuthenticationUtil authenticationUtil;

    @Transactional
    public JobPostResponse createJobPost(JobPostRequest request) {
        // Token'dan user ID'yi otomatik al
        String currentUserId = authenticationUtil.getCurrentUserId();
        
        JobPost jobPost = new JobPost();
        jobPost.setOwnerId(currentUserId);  // Token'dan alınan user ID
        jobPost.setPostTitle(request.getPostTitle());
        jobPost.setDepartment(request.getDepartment());
        jobPost.setLocation(request.getLocation());
        jobPost.setSalary(request.getSalary());
        jobPost.setJobType(request.getJobType());
        jobPost.setPublishDate(request.getPublishDate());
        jobPost.setLastApplicationDate(request.getLastApplicationDate());
        jobPost.setJobDefinition(request.getJobDefinition());
        jobPost.setRequiredSkills(request.getRequiredSkills());
        jobPost.setPostStatus(request.getPostStatus());

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        
        JobPostResponse response = new JobPostResponse(savedJobPost);
        response.setApplicationCount(0L);
        return response;
    }

    @Transactional
    public JobPostResponse updateJobPost(Integer id, JobPostRequest request) {
        String currentUserId = authenticationUtil.getCurrentUserId();
        
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İş ilanı bulunamadı: " + id));

        // Sadece sahibi olan kullanıcı güncelleyebilir
        if (!jobPost.getOwnerId().equals(currentUserId)) {
            throw new RuntimeException("Bu iş ilanını güncelleme yetkiniz yok");
        }

        jobPost.setPostTitle(request.getPostTitle());
        jobPost.setDepartment(request.getDepartment());
        jobPost.setLocation(request.getLocation());
        jobPost.setSalary(request.getSalary());
        jobPost.setJobType(request.getJobType());
        jobPost.setPublishDate(request.getPublishDate());
        jobPost.setLastApplicationDate(request.getLastApplicationDate());
        jobPost.setJobDefinition(request.getJobDefinition());
        jobPost.setRequiredSkills(request.getRequiredSkills());
        jobPost.setPostStatus(request.getPostStatus());

        JobPost updatedJobPost = jobPostRepository.save(jobPost);
        
        JobPostResponse response = new JobPostResponse(updatedJobPost);
        response.setApplicationCount(jobApplicationRepository.countByJobPostingId(id));
        return response;
    }

    @Transactional
    public void deleteJobPost(Integer id) {
        String currentUserId = authenticationUtil.getCurrentUserId();
        
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İş ilanı bulunamadı: " + id));
        
        // Sadece sahibi olan kullanıcı silebilir
        if (!jobPost.getOwnerId().equals(currentUserId)) {
            throw new RuntimeException("Bu iş ilanını silme yetkiniz yok");
        }
        
        jobPostRepository.deleteById(id);
    }

    public JobPostResponse getJobPostById(Integer id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İş ilanı bulunamadı: " + id));
        
        JobPostResponse response = new JobPostResponse(jobPost);
        response.setApplicationCount(jobApplicationRepository.countByJobPostingId(id));
        return response;
    }

    public List<JobPostResponse> getAllJobPosts() {
        return jobPostRepository.findAll().stream()
                .map(jobPost -> {
                    JobPostResponse response = new JobPostResponse(jobPost);
                    response.setApplicationCount(jobApplicationRepository.countByJobPostingId(jobPost.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<JobPostResponse> getJobPostsByOwnerId(String ownerId) {
        String currentUserId = authenticationUtil.getCurrentUserId();
        
        // Kullanıcı sadece kendi ilanlarını görebilir
        if (!ownerId.equals(currentUserId)) {
            throw new RuntimeException("Bu iş ilanlarını görüntüleme yetkiniz yok");
        }
        
        return jobPostRepository.findByOwnerId(ownerId).stream()
                .map(jobPost -> {
                    JobPostResponse response = new JobPostResponse(jobPost);
                    response.setApplicationCount(jobApplicationRepository.countByJobPostingId(jobPost.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<JobPostResponse> getJobPostsByStatus(JobPost.PostStatus status) {
        return jobPostRepository.findByPostStatus(status).stream()
                .map(jobPost -> {
                    JobPostResponse response = new JobPostResponse(jobPost);
                    response.setApplicationCount(jobApplicationRepository.countByJobPostingId(jobPost.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public JobPostResponse updateJobPostStatus(Integer id, JobPost.PostStatus status) {
        String currentUserId = authenticationUtil.getCurrentUserId();
        
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İş ilanı bulunamadı: " + id));

        // Sadece sahibi olan kullanıcı durumu değiştirebilir
        if (!jobPost.getOwnerId().equals(currentUserId)) {
            throw new RuntimeException("Bu iş ilanının durumunu değiştirme yetkiniz yok");
        }

        jobPost.setPostStatus(status);
        JobPost updatedJobPost = jobPostRepository.save(jobPost);
        
        JobPostResponse response = new JobPostResponse(updatedJobPost);
        response.setApplicationCount(jobApplicationRepository.countByJobPostingId(id));
        return response;
    }

    /**
     * Kullanıcının kendi iş ilanlarını getirir
     */
    public List<JobPostResponse> getMyJobPosts() {
        String currentUserId = authenticationUtil.getCurrentUserId();
        return getJobPostsByOwnerId(currentUserId);
    }
}
