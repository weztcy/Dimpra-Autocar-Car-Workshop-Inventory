package com.workshop.model;

import java.sql.Date;
import java.sql.Timestamp;

public class StockAdjustment {
    private int id;
    private String adjustmentCode;
    private Date adjustmentDate;
    private int partId;
    private String partNumber;
    private String partName;
    private String adjustmentType; // INCREASE, DECREASE
    private int quantity;
    private String reason; // DAMAGED_PART, EXPIRED_FLUID, PHYSICAL_COUNT_VARIANCE, RETURN_FROM_CUSTOMER, SCRAP_OBSOLETE, OTHER
    private String notes;
    private int approvedBy;
    private String approvedByName;
    private Timestamp createdAt;

    public StockAdjustment() {
        this.adjustmentType = "DECREASE";
        this.reason = "PHYSICAL_COUNT_VARIANCE";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAdjustmentCode() { return adjustmentCode; }
    public void setAdjustmentCode(String adjustmentCode) { this.adjustmentCode = adjustmentCode; }

    public Date getAdjustmentDate() { return adjustmentDate; }
    public void setAdjustmentDate(Date adjustmentDate) { this.adjustmentDate = adjustmentDate; }

    public int getPartId() { return partId; }
    public void setPartId(int partId) { this.partId = partId; }

    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getAdjustmentType() { return adjustmentType; }
    public void setAdjustmentType(String adjustmentType) { this.adjustmentType = adjustmentType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getApprovedBy() { return approvedBy; }
    public void setApprovedBy(int approvedBy) { this.approvedBy = approvedBy; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
