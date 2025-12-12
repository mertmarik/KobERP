package com.koberp.jobpost.dto;

import com.koberp.jobpost.entity.JobPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostResponse {

    private Integer id;
    private String ownerId;
    private String postTitle;
    private String department;
    private String location;
    private String salary;
    private String jobType;
    private LocalDate publishDate;
    private LocalDate lastApplicationDate;
    private String jobDefinition;
    private String requiredSkills;
    private JobPost.PostStatus postStatus;
    private Long applicationCount;

    public JobPostResponse(JobPost jobPost) {
        this.id = jobPost.getId();
        this.ownerId = jobPost.getOwnerId();
        this.postTitle = jobPost.getPostTitle();
        this.department = jobPost.getDepartment();
        this.location = jobPost.getLocation();
        this.salary = jobPost.getSalary();
        this.jobType = jobPost.getJobType();
        this.publishDate = jobPost.getPublishDate();
        this.lastApplicationDate = jobPost.getLastApplicationDate();
        this.jobDefinition = jobPost.getJobDefinition();
        this.requiredSkills = jobPost.getRequiredSkills();
        this.postStatus = jobPost.getPostStatus();
    }
}
