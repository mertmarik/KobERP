package com.koberp.jobpost.controller;

import com.koberp.jobpost.dto.JobPostRequest;
import com.koberp.jobpost.dto.JobPostResponse;
import com.koberp.jobpost.dto.JobPostStatusUpdateRequest;
import com.koberp.jobpost.entity.JobPost;
import com.koberp.jobpost.service.JobPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/job-posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "İş İlanı Yönetimi", description = "İş ilanlarını oluşturma, güncelleme, silme ve sorgulama işlemleri")
@SecurityRequirement(name = "bearerAuth")
public class JobPostController {

    private final JobPostService jobPostService;

    @Operation(
            summary = "Yeni İş İlanı Oluştur",
            description = "Giriş yapan kullanıcı için yeni bir iş ilanı oluşturur. Owner ID otomatik olarak JWT token'dan alınır."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "İş ilanı başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek parametreleri"),
            @ApiResponse(responseCode = "401", description = "Yetkisiz erişim")
    })
    @PostMapping
    public ResponseEntity<JobPostResponse> createJobPost(@Valid @RequestBody JobPostRequest request) {
        JobPostResponse response = jobPostService.createJobPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Tüm İş İlanlarını Listele",
            description = "Sistemdeki tüm iş ilanlarını getirir"
    )
    @GetMapping
    public ResponseEntity<List<JobPostResponse>> getAllJobPosts() {
        List<JobPostResponse> jobPosts = jobPostService.getAllJobPosts();
        return ResponseEntity.ok(jobPosts);
    }

    @Operation(
            summary = "İş İlanı Detayı",
            description = "ID'ye göre belirli bir iş ilanının detaylarını getirir"
    )
    @GetMapping("/{id}")
    public ResponseEntity<JobPostResponse> getJobPostById(
            @Parameter(description = "İş ilanı ID'si") @PathVariable Integer id
    ) {
        JobPostResponse jobPost = jobPostService.getJobPostById(id);
        return ResponseEntity.ok(jobPost);
    }

    @Operation(
            summary = "Kullanıcıya Göre İş İlanları",
            description = "Belirli bir kullanıcıya ait iş ilanlarını getirir (Sadece kendi ilanlarını görebilir)"
    )
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<JobPostResponse>> getJobPostsByOwnerId(
            @Parameter(description = "Kullanıcı ID'si") @PathVariable String ownerId
    ) {
        List<JobPostResponse> jobPosts = jobPostService.getJobPostsByOwnerId(ownerId);
        return ResponseEntity.ok(jobPosts);
    }

    @Operation(
            summary = "Benim İş İlanlarım",
            description = "Giriş yapan kullanıcının kendi iş ilanlarını getirir"
    )
    @GetMapping("/my-posts")
    public ResponseEntity<List<JobPostResponse>> getMyJobPosts() {
        List<JobPostResponse> jobPosts = jobPostService.getMyJobPosts();
        return ResponseEntity.ok(jobPosts);
    }

    @Operation(
            summary = "Duruma Göre İş İlanları",
            description = "İş ilanlarını durumuna göre filtreler (OPEN: Açık, PENDING: Beklemede, CLOSED: Kapalı)"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobPostResponse>> getJobPostsByStatus(
            @Parameter(description = "İş ilanı durumu") @PathVariable JobPost.PostStatus status
    ) {
        List<JobPostResponse> jobPosts = jobPostService.getJobPostsByStatus(status);
        return ResponseEntity.ok(jobPosts);
    }

    @Operation(
            summary = "İş İlanını Güncelle",
            description = "Mevcut bir iş ilanını günceller (Sadece ilanın sahibi güncelleyebilir)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "İş ilanı başarıyla güncellendi"),
            @ApiResponse(responseCode = "403", description = "Bu iş ilanını güncelleme yetkiniz yok"),
            @ApiResponse(responseCode = "404", description = "İş ilanı bulunamadı")
    })
    @PutMapping("/{id}")
    public ResponseEntity<JobPostResponse> updateJobPost(
            @Parameter(description = "İş ilanı ID'si") @PathVariable Integer id,
            @Valid @RequestBody JobPostRequest request
    ) {
        JobPostResponse response = jobPostService.updateJobPost(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "İş İlanı Durumunu Güncelle",
            description = "İş ilanının durumunu değiştirir (OPEN, PENDING, CLOSED)"
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<JobPostResponse> updateJobPostStatus(
            @Parameter(description = "İş ilanı ID'si") @PathVariable Integer id,
            @Valid @RequestBody JobPostStatusUpdateRequest request
    ) {
        JobPostResponse response = jobPostService.updateJobPostStatus(id, request.getPostStatus());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "İş İlanını Sil",
            description = "Bir iş ilanını siler (Sadece ilanın sahibi silebilir)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "İş ilanı başarıyla silindi"),
            @ApiResponse(responseCode = "403", description = "Bu iş ilanını silme yetkiniz yok"),
            @ApiResponse(responseCode = "404", description = "İş ilanı bulunamadı")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPost(
            @Parameter(description = "İş ilanı ID'si") @PathVariable Integer id
    ) {
        jobPostService.deleteJobPost(id);
        return ResponseEntity.noContent().build();
    }
}
