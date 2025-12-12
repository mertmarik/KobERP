package com.koberp.jobpost.repository;

import com.koberp.jobpost.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Integer> {
    
    List<JobPost> findByOwnerId(String ownerId);
    
    List<JobPost> findByPostStatus(JobPost.PostStatus postStatus);
    
    List<JobPost> findByOwnerIdAndPostStatus(String ownerId, JobPost.PostStatus postStatus);
}
