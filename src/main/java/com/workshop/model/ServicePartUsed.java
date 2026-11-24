package com.workshop.model;

import java.math.BigDecimal;

public class ServicePartUsed {
    private int id;
    private int serviceTransactionId;
    private int partId;
    private String partNumber;
    private String partName;
    private String brand;
    private String unitOfMeasure;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public ServicePartUsed() {
        this.quantity = 1;
        this.unitPrice = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getServiceTransactionId() { return serviceTransactionId; }
    public void setServiceTransactionId(int serviceTransactionId) { this.serviceTransactionId = serviceTransactionId; }

    public int getPartId() { return partId; }
    public void setPartId(int partId) { this.partId = partId; }

    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateSubtotal();
    }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        recalculateSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public void recalculateSubtotal() {
        if (unitPrice != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
