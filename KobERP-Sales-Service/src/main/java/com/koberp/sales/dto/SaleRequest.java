package com.koberp.sales.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequest {

    @NotNull(message = "Stock ID is required")
    private Long stockId;

    @NotNull(message = "Sale price is required")
    @Positive(message = "Sale price must be positive")
    private Integer salePrice;

    private Integer profit;

    private LocalDate lastSaleDate;

    @NotNull(message = "Sale quantity is required")
    private String saleQuantity;

    private Boolean documentUploaded;

    private String customerName;

    private String customerPhone;
}
