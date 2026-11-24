package com.workshop.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ServiceTransaction {
    private int id;
    private String transactionCode;
    private Timestamp transactionDate;
    private int vehicleId;
    private String plateNumber;
    private String vehicleModel;
    private String customerName;
    private String customerPhone;
    private int technicianId;
    private String technicianName;
    private String serviceType;
    private int odometerIn;
    private String customerComplaint;
    private String technicianDiagnosis;
    private BigDecimal partsTotal;
    private BigDecimal laborTotal;
    private BigDecimal discount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String paymentMethod; // CASH, TRANSFER, DEBIT_CARD, CREDIT_CARD, QRIS
    private String paymentStatus; // UNPAID, PAID
    private int createdBy;
    private String createdByName;
    private Timestamp createdAt;

    private List<ServicePartUsed> partsUsed = new ArrayList<>();
    private List<ServiceLaborItem> laborItems = new ArrayList<>();

    public ServiceTransaction() {
        this.partsTotal = BigDecimal.ZERO;
        this.laborTotal = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.grandTotal = BigDecimal.ZERO;
        this.status = "PENDING";
        this.paymentMethod = "CASH";
        this.paymentStatus = "UNPAID";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

    public Timestamp getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Timestamp transactionDate) { this.transactionDate = transactionDate; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public int getTechnicianId() { return technicianId; }
    public void setTechnicianId(int technicianId) { this.technicianId = technicianId; }

    public String getTechnicianName() { return technicianName; }
    public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public int getOdometerIn() { return odometerIn; }
    public void setOdometerIn(int odometerIn) { this.odometerIn = odometerIn; }

    public String getCustomerComplaint() { return customerComplaint; }
    public void setCustomerComplaint(String customerComplaint) { this.customerComplaint = customerComplaint; }

    public String getTechnicianDiagnosis() { return technicianDiagnosis; }
    public void setTechnicianDiagnosis(String technicianDiagnosis) { this.technicianDiagnosis = technicianDiagnosis; }

    public BigDecimal getPartsTotal() { return partsTotal; }
    public void setPartsTotal(BigDecimal partsTotal) { this.partsTotal = partsTotal; }

    public BigDecimal getLaborTotal() { return laborTotal; }
    public void setLaborTotal(BigDecimal laborTotal) { this.laborTotal = laborTotal; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<ServicePartUsed> getPartsUsed() { return partsUsed; }
    public void setPartsUsed(List<ServicePartUsed> partsUsed) { this.partsUsed = partsUsed; }

    public List<ServiceLaborItem> getLaborItems() { return laborItems; }
    public void setLaborItems(List<ServiceLaborItem> laborItems) { this.laborItems = laborItems; }

    public void recalculateTotals() {
        BigDecimal pSum = BigDecimal.ZERO;
        for (ServicePartUsed pu : partsUsed) {
            if (pu.getSubtotal() != null) pSum = pSum.add(pu.getSubtotal());
        }
        this.partsTotal = pSum;

        BigDecimal lSum = BigDecimal.ZERO;
        for (ServiceLaborItem li : laborItems) {
            if (li.getSubtotal() != null) lSum = lSum.add(li.getSubtotal());
        }
        this.laborTotal = lSum;

        BigDecimal sub = partsTotal.add(laborTotal);
        if (discount != null) {
            sub = sub.subtract(discount);
        }
        if (sub.compareTo(BigDecimal.ZERO) < 0) sub = BigDecimal.ZERO;

        // PPN 11%
        this.taxAmount = sub.multiply(new BigDecimal("0.11")).setScale(0, BigDecimal.ROUND_HALF_UP);
        this.grandTotal = sub.add(this.taxAmount);
    }

    @Override
    public String toString() {
        return transactionCode + " - " + plateNumber + " (" + customerName + ")";
    }
}
