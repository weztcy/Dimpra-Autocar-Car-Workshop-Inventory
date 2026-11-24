package com.workshop.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrder {
    private int id;
    private String poNumber;
    private int supplierId;
    private String supplierName;
    private String supplierPhone;
    private Date orderDate;
    private Date expectedDate;
    private Date receivedDate;
    private String status; // DRAFT, ORDERED, RECEIVED_PARTIAL, RECEIVED_FULL, CANCELLED
    private String paymentStatus; // UNPAID, PARTIALLY_PAID, PAID
    private BigDecimal totalAmount;
    private String notes;
    private int createdBy;
    private String createdByName;
    private Timestamp createdAt;
    private List<PurchaseOrderItem> items = new ArrayList<>();

    public PurchaseOrder() {
        this.status = "DRAFT";
        this.paymentStatus = "UNPAID";
        this.totalAmount = BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getSupplierPhone() { return supplierPhone; }
    public void setSupplierPhone(String supplierPhone) { this.supplierPhone = supplierPhone; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public Date getExpectedDate() { return expectedDate; }
    public void setExpectedDate(Date expectedDate) { this.expectedDate = expectedDate; }

    public Date getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Date receivedDate) { this.receivedDate = receivedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<PurchaseOrderItem> getItems() { return items; }
    public void setItems(List<PurchaseOrderItem> items) { this.items = items; }

    public void recalculateTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (PurchaseOrderItem it : items) {
            if (it.getSubtotal() != null) {
                sum = sum.add(it.getSubtotal());
            }
        }
        this.totalAmount = sum;
    }

    @Override
    public String toString() {
        return poNumber + " - " + supplierName;
    }
}
