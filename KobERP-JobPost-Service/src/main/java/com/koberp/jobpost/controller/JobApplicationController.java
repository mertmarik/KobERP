package com.koberp.jobpost.controller;

import com.koberp.jobpost.dto.ApplicationStatusUpdateRequest;
import com.koberp.jobpost.dto.JobApplicationRequest;
import com.koberp.jobpost.dto.JobApplicationResponse;
import com.koberp.jobpost.entity.JobApplication;
import com.koberp.jobpost.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Başvuru Yönetimi", description = "İş ilanı başvurularını yönetme işlemleri")
@SecurityRequirement(name = "bearerAuth")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @Operation(
            summary = "Yeni Başvuru Oluştur",
            description = "Bir iş ilanına yeni başvuru oluşturur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Başvuru başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek parametreleri")
    })
    @PostMapping
    public ResponseEntity<JobApplicationResponse> createApplication(@Valid @RequestBody JobApplicationRequest request) {
        JobApplicationResponse response = jobApplicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Tüm Başvuruları Listele",
            description = "Sistemdeki tüm başvuruları getirir"
    )
    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAllApplications() {
        List<JobApplicationResponse> applications = jobApplicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @Operation(
            summary = "Başvuru Detayı",
            description = "ID'ye göre belirli bir başvurunun detaylarını getirir"
    )
    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getApplicationById(
            @Parameter(description = "Başvuru ID'si") @PathVariable Integer id
    ) {
        JobApplicationResponse application = jobApplicationService.getApplicationById(id);
        return ResponseEntity.ok(application);
    }

    @Operation(
            summary = "İş İlanına Göre Başvurular",
            description = "Belirli bir iş ilanına yapılan tüm başvuruları getirir (Sadece ilanın sahibi görebilir)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Başarılı"),
            @ApiResponse(responseCode = "403", description = "Bu iş ilanının başvurularını görüntüleme yetkiniz yok")
    })
    @GetMapping("/job-post/{jobPostingId}")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsByJobPostId(
            @Parameter(description = "İş ilanı ID'si") @PathVariable Integer jobPostingId
    ) {
        List<JobApplicationResponse> applications = jobApplicationService.getApplicationsByJobPostId(jobPostingId);
        return ResponseEntity.ok(applications);
    }

    @Operation(
            summary = "Duruma Göre Başvurular",
            description = "Başvuruları durumuna göre filtreler (PENDING: Bekliyor, ACCEPTED: Kabul Edildi, REJECTED: Reddedildi)"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsByStatus(
            @Parameter(description = "Başvuru durumu") @PathVariable JobApplication.ApplicationStatus status
    ) {
        List<JobApplicationResponse> applications = jobApplicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }

    @Operation(
            summary = "Başvuruyu Kabul Et",
            description = "Bir başvuruyu kabul eder (Sadece ilanın sahibi kabul edebilir)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Başvuru başarıyla kabul edildi"),
            @ApiResponse(responseCode = "403", description = "Bu işlemi gerçekleştirme yetkiniz yok"),
            @ApiResponse(responseCode = "404", description = "Başvuru bulunamadı")
    })
    @PatchMapping("/{id}/accept")
    public ResponseEntity<JobApplicationResponse> acceptApplication(
            @Parameter(description = "Başvuru ID'si") @PathVariable Integer id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request
    ) {
        JobApplicationResponse response = jobApplicationService.acceptApplication(
                id,
                request.getReviewedBy(),
                request.getStatusNotes()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Başvuruyu Reddet",
            description = "Bir başvuruyu reddeder (Sadece ilanın sahibi reddedebilir)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Başvuru başarıyla reddedildi"),
            @ApiResponse(responseCode = "403", description = "Bu işlemi gerçekleştirme yetkiniz yok"),
            @ApiResponse(responseCode = "404", description = "Başvuru bulunamadı")
    })
    @PatchMapping("/{id}/reject")
    public ResponseEntity<JobApplicationResponse> rejectApplication(
            @Parameter(description = "Başvuru ID'si") @PathVariable Integer id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request
    ) {
        JobApplicationResponse response = jobApplicationService.rejectApplication(
                id,
                request.getReviewedBy(),
                request.getStatusNotes()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Başvuru Durumunu Güncelle",
            description = "Başvurunun durumunu günceller"
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            @Parameter(description = "Başvuru ID'si") @PathVariable Integer id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request
    ) {
        JobApplicationResponse response = jobApplicationService.updateApplicationStatus(
                id,
                request.getApplicationStatus(),
                request.getReviewedBy(),
                request.getStatusNotes()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Başvuruyu Sil",
            description = "Bir başvuruyu siler (Sadece ilanın sahibi silebilir)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Başvuru başarıyla silindi"),
            @ApiResponse(responseCode = "403", description = "Bu işlemi gerçekleştirme yetkiniz yok"),
            @ApiResponse(responseCode = "404", description = "Başvuru bulunamadı")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @Parameter(description = "Başvuru ID'si") @PathVariable Integer id
    ) {
        jobApplicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}
