package com.koberp.sales.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_items", schema = "erp")
@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 200, updatable = false, insertable = false)
    private String name;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit", length = 50, updatable = false, insertable = false)
    private String unit;

    @Column(name = "unit_price", precision = 12, scale = 2, updatable = false, insertable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_value", precision = 15, scale = 2, updatable = false, insertable = false)
    private BigDecimal totalValue;

    @Column(name = "type", length = 100, updatable = false, insertable = false)
    private String type;

    @Column(name = "supplier", length = 200, updatable = false, insertable = false)
    private String supplier;

    @Column(name = "min_stock_level", updatable = false, insertable = false)
    private Integer minStockLevel;

    @Column(name = "max_stock_level", updatable = false, insertable = false)
    private Integer maxStockLevel;

    @Column(name = "barcode", length = 100, updatable = false, insertable = false)
    private String barcode;

    @Column(name = "description", updatable = false, insertable = false)
    private String description;

    @Column(name = "location", length = 100, updatable = false, insertable = false)
    private String location;

    @Column(name = "is_active", updatable = false, insertable = false)
    private Boolean isActive;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
