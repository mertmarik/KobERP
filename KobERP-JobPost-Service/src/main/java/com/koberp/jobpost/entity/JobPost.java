package com.koberp.jobpost.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "job_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "post_title", nullable = false)
    private String postTitle;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "salary")
    private String salary;

    @Column(name = "job_type", nullable = false)
    private String jobType;

    @Column(name = "publish_date", nullable = false)
    private LocalDate publishDate;

    @Column(name = "last_application_date")
    private LocalDate lastApplicationDate;

    @Column(name = "job_definition", columnDefinition = "TEXT")
    private String jobDefinition;

    @Column(name = "required_skills")
    private String requiredSkills;

    @Column(name = "post_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    public enum PostStatus {
        OPEN,      // Açık
        PENDING,   // Beklemede
        CLOSED     // Kapalı
    }
}
