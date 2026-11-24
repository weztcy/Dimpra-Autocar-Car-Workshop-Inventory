package com.workshop.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class StockLedger {
    private int id;
    private Timestamp transactionTime;
    private int partId;
    private String partNumber;
    private String partName;
    private String transactionType; // PROCUREMENT_IN, SERVICE_OUT, ADJUSTMENT_ADD, ADJUSTMENT_SUB, RETURN_IN, SCRAP_OUT
    private String referenceNo;
    private int quantityChange;
    private int stockBefore;
    private int stockAfter;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String notes;
    private int createdBy;
    private String createdByName;

    public StockLedger() {
        this.unitCost = BigDecimal.ZERO;
        this.totalCost = BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Timestamp getTransactionTime() { return transactionTime; }
    public void setTransactionTime(Timestamp transactionTime) { this.transactionTime = transactionTime; }

    public int getPartId() { return partId; }
    public void setPartId(int partId) { this.partId = partId; }

    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

    public int getQuantityChange() { return quantityChange; }
    public void setQuantityChange(int quantityChange) { this.quantityChange = quantityChange; }

    public int getStockBefore() { return stockBefore; }
    public void setStockBefore(int stockBefore) { this.stockBefore = stockBefore; }

    public int getStockAfter() { return stockAfter; }
    public void setStockAfter(int stockAfter) { this.stockAfter = stockAfter; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
}
