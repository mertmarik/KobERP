package com.koberp.jobpost.dto;

import com.koberp.jobpost.entity.JobApplication;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {

    @NotNull(message = "İş ilanı ID gereklidir")
    private Integer jobPostingId;

    @NotBlank(message = "Başvuran adı gereklidir")
    private String applicantName;

    @NotBlank(message = "E-posta adresi gereklidir")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String applicantEmail;

    private String applicantPhone;

    private Double yearsExperience;

    private String cvFilePath;

    private String coverLetter;

    private JobApplication.ApplicationStatus applicationStatus;
}
