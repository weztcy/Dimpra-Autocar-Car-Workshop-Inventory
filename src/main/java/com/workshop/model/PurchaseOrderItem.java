package com.workshop.model;

import java.math.BigDecimal;

public class PurchaseOrderItem {
    private int id;
    private int poId;
    private int partId;
    private String partNumber;
    private String partName;
    private String brand;
    private String unitOfMeasure;
    private int quantityOrdered;
    private int quantityReceived;
    private BigDecimal unitCost;
    private BigDecimal subtotal;

    public PurchaseOrderItem() {
        this.quantityOrdered = 1;
        this.quantityReceived = 0;
        this.unitCost = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPoId() { return poId; }
    public void setPoId(int poId) { this.poId = poId; }

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

    public int getQuantityOrdered() { return quantityOrdered; }
    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
        recalculateSubtotal();
    }

    public int getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(int quantityReceived) { this.quantityReceived = quantityReceived; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
        recalculateSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public void recalculateSubtotal() {
        if (unitCost != null) {
            this.subtotal = unitCost.multiply(BigDecimal.valueOf(quantityOrdered));
        }
    }
}
