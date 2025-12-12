package com.koberp.jobpost.dto;

import com.koberp.jobpost.entity.JobPost;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostRequest {

    // ownerId artık request'te gelmeyecek, JWT token'dan alınacak

    @NotBlank(message = "İş ilanı başlığı gereklidir")
    private String postTitle;

    @NotBlank(message = "Departman gereklidir")
    private String department;

    @NotBlank(message = "Lokasyon gereklidir")
    private String location;

    private String salary;

    @NotBlank(message = "İş türü gereklidir")
    private String jobType;

    @NotNull(message = "Yayın tarihi gereklidir")
    private LocalDate publishDate;

    private LocalDate lastApplicationDate;

    private String jobDefinition;

    private String requiredSkills;

    @NotNull(message = "İlan durumu gereklidir")
    private JobPost.PostStatus postStatus;
}
