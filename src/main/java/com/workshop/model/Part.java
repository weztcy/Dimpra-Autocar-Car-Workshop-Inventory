package com.workshop.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Part {
    private int id;
    private String partNumber;
    private String partName;
    private int categoryId;
    private String categoryName;
    private String brand;
    private String compatibility;
    private String unitOfMeasure;
    private String storageBin;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private int minStock;
    private int currentStock;
    private PartStatus status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Part() {
        this.costPrice = BigDecimal.ZERO;
        this.sellingPrice = BigDecimal.ZERO;
        this.unitOfMeasure = "PCS";
        this.storageBin = "A-01-01";
        this.status = PartStatus.IN_STOCK;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCompatibility() { return compatibility; }
    public void setCompatibility(String compatibility) { this.compatibility = compatibility; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public String getStorageBin() { return storageBin; }
    public void setStorageBin(String storageBin) { this.storageBin = storageBin; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public int getMinStock() { return minStock; }
    public void setMinStock(int minStock) { this.minStock = minStock; }

    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
        recalculateStatus();
    }

    public PartStatus getStatus() { return status; }
    public void setStatus(PartStatus status) { this.status = status; }

    public void recalculateStatus() {
        if (this.currentStock <= 0) {
            this.status = PartStatus.OUT_OF_STOCK;
        } else if (this.currentStock <= this.minStock) {
            this.status = PartStatus.LOW_STOCK;
        } else {
            this.status = PartStatus.IN_STOCK;
        }
    }

    public BigDecimal getStockValuation() {
        if (costPrice == null) return BigDecimal.ZERO;
        return costPrice.multiply(BigDecimal.valueOf(currentStock));
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return partNumber + " - " + partName + " (" + brand + ")";
    }
}
