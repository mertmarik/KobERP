package com.koberp.jobpost.dto;

import com.koberp.jobpost.entity.JobApplication;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusUpdateRequest {

    @NotNull(message = "Başvuru durumu gereklidir")
    private JobApplication.ApplicationStatus applicationStatus;

    private String statusNotes;

    private Integer reviewedBy;
}
