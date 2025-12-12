package com.koberp.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {

    private Long id;
    private Long stockId;
    private Integer salePrice;
    private Integer profit;
    private LocalDate lastSaleDate;
    private String saleQuantity;
    private Boolean documentUploaded;
    private String customerName;
    private String customerPhone;
}
