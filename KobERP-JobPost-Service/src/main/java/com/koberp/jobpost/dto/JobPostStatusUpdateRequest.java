package com.koberp.jobpost.dto;

import com.koberp.jobpost.entity.JobPost;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostStatusUpdateRequest {

    @NotNull(message = "İlan durumu gereklidir")
    private JobPost.PostStatus postStatus;
}
