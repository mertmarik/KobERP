package com.koberp.sales.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "sales", schema = "erp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    @Column(name = "sale_price")
    private Integer salePrice;

    @Column(name = "profit")
    private Integer profit;

    @Column(name = "last_sale_date")
    private LocalDate lastSaleDate;

    @Column(name = "sale_quantity", length = 255)
    private String saleQuantity;

    @Column(name = "document_uploaded", nullable = false)
    private Boolean documentUploaded = false;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "customer_phone", length = 255)
    private String customerPhone;

    @PrePersist
    protected void onCreate() {
        if (lastSaleDate == null) {
            lastSaleDate = LocalDate.now();
        }
    }
}
