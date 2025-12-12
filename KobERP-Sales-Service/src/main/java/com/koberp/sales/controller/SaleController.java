package com.koberp.sales.controller;

import com.koberp.sales.dto.SaleRequest;
import com.koberp.sales.dto.SaleResponse;
import com.koberp.sales.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @Operation(summary = "Create a new sale", description = "Creates a new sale and updates stock quantity")
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest request) {
        SaleResponse response = saleService.createSale(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all sales", description = "Retrieves all sales records")
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        List<SaleResponse> sales = saleService.getAllSales();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale by ID", description = "Retrieves a specific sale by its ID")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable Long id) {
        SaleResponse response = saleService.getSaleById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sale", description = "Updates an existing sale and adjusts stock accordingly")
    public ResponseEntity<SaleResponse> updateSale(
            @PathVariable Long id,
            @Valid @RequestBody SaleRequest request) {
        SaleResponse response = saleService.updateSale(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sale", description = "Deletes a sale and restores stock quantity")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }
}
