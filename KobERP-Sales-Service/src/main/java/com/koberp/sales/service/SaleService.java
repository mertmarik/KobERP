package com.koberp.sales.service;

import com.koberp.sales.dto.SaleRequest;
import com.koberp.sales.dto.SaleResponse;
import com.koberp.sales.entity.Sale;
import com.koberp.sales.entity.StockItem;
import com.koberp.sales.exception.InsufficientStockException;
import com.koberp.sales.exception.ResourceNotFoundException;
import com.koberp.sales.repository.SaleRepository;
import com.koberp.sales.repository.StockItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final StockItemRepository stockItemRepository;

    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        // Stok kontrolü
        StockItem stockItem = stockItemRepository.findById(request.getStockId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock item not found with id: " + request.getStockId()));

        // Satış miktarını parse et
        int saleQuantity = parseSaleQuantity(request.getSaleQuantity());

        // Yeterli stok var mı kontrol et
        if (stockItem.getQuantity() < saleQuantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock. Available: %d, Requested: %d", 
                            stockItem.getQuantity(), saleQuantity)
            );
        }

        // Sale entity oluştur
        Sale sale = new Sale();
        sale.setStockId(request.getStockId());
        sale.setSalePrice(request.getSalePrice());
        sale.setProfit(request.getProfit());
        sale.setLastSaleDate(request.getLastSaleDate() != null ? request.getLastSaleDate() : LocalDate.now());
        sale.setSaleQuantity(request.getSaleQuantity());
        sale.setDocumentUploaded(request.getDocumentUploaded() != null ? request.getDocumentUploaded() : false);
        sale.setCustomerName(request.getCustomerName());
        sale.setCustomerPhone(request.getCustomerPhone());

        // Stok miktarını azalt ve updated_at'i güncelle
        stockItem.setQuantity(stockItem.getQuantity() - saleQuantity);
        stockItem.setUpdatedAt(LocalDateTime.now());
        stockItemRepository.save(stockItem);

        // Satışı kaydet
        Sale savedSale = saleRepository.save(sale);

        return mapToResponse(savedSale);
    }

    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
        return mapToResponse(sale);
    }

    @Transactional
    public SaleResponse updateSale(Long id, SaleRequest request) {
        Sale existingSale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        // Eski satış miktarını geri ekle
        int oldQuantity = parseSaleQuantity(existingSale.getSaleQuantity());
        StockItem stockItem = stockItemRepository.findById(existingSale.getStockId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock item not found"));
        stockItem.setQuantity(stockItem.getQuantity() + oldQuantity);

        // Yeni stock_id kontrolü (eğer değişmişse)
        if (!existingSale.getStockId().equals(request.getStockId())) {
            stockItem.setUpdatedAt(LocalDateTime.now());
            stockItemRepository.save(stockItem); // Eski stoku güncelle
            
            stockItem = stockItemRepository.findById(request.getStockId())
                    .orElseThrow(() -> new ResourceNotFoundException("New stock item not found with id: " + request.getStockId()));
        }

        // Yeni satış miktarını kontrol et
        int newQuantity = parseSaleQuantity(request.getSaleQuantity());
        if (stockItem.getQuantity() < newQuantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock. Available: %d, Requested: %d", 
                            stockItem.getQuantity(), newQuantity)
            );
        }

        // Sale güncelle
        existingSale.setStockId(request.getStockId());
        existingSale.setSalePrice(request.getSalePrice());
        existingSale.setProfit(request.getProfit());
        existingSale.setLastSaleDate(request.getLastSaleDate() != null ? request.getLastSaleDate() : existingSale.getLastSaleDate());
        existingSale.setSaleQuantity(request.getSaleQuantity());
        existingSale.setDocumentUploaded(request.getDocumentUploaded() != null ? request.getDocumentUploaded() : existingSale.getDocumentUploaded());
        existingSale.setCustomerName(request.getCustomerName());
        existingSale.setCustomerPhone(request.getCustomerPhone());

        // Yeni stok miktarını azalt ve updated_at'i güncelle
        stockItem.setQuantity(stockItem.getQuantity() - newQuantity);
        stockItem.setUpdatedAt(LocalDateTime.now());
        stockItemRepository.save(stockItem);

        Sale updatedSale = saleRepository.save(existingSale);
        return mapToResponse(updatedSale);
    }

    @Transactional
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        // Stok miktarını geri ekle ve updated_at'i güncelle
        int saleQuantity = parseSaleQuantity(sale.getSaleQuantity());
        StockItem stockItem = stockItemRepository.findById(sale.getStockId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock item not found"));
        
        stockItem.setQuantity(stockItem.getQuantity() + saleQuantity);
        stockItem.setUpdatedAt(LocalDateTime.now());
        stockItemRepository.save(stockItem);

        // Satışı sil
        saleRepository.delete(sale);
    }

    private int parseSaleQuantity(String saleQuantity) {
        try {
            return Integer.parseInt(saleQuantity);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid sale quantity format: " + saleQuantity);
        }
    }

    private SaleResponse mapToResponse(Sale sale) {
        SaleResponse response = new SaleResponse();
        response.setId(sale.getId());
        response.setStockId(sale.getStockId());
        response.setSalePrice(sale.getSalePrice());
        response.setProfit(sale.getProfit());
        response.setLastSaleDate(sale.getLastSaleDate());
        response.setSaleQuantity(sale.getSaleQuantity());
        response.setDocumentUploaded(sale.getDocumentUploaded());
        response.setCustomerName(sale.getCustomerName());
        response.setCustomerPhone(sale.getCustomerPhone());
        return response;
    }
}
