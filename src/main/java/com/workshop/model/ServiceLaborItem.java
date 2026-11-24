package com.workshop.model;

import java.math.BigDecimal;

public class ServiceLaborItem {
    private int id;
    private int serviceTransactionId;
    private String serviceName;
    private BigDecimal hours;
    private BigDecimal rate;
    private BigDecimal subtotal;

    public ServiceLaborItem() {
        this.hours = BigDecimal.ONE;
        this.rate = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getServiceTransactionId() { return serviceTransactionId; }
    public void setServiceTransactionId(int serviceTransactionId) { this.serviceTransactionId = serviceTransactionId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public BigDecimal getHours() { return hours; }
    public void setHours(BigDecimal hours) {
        this.hours = hours;
        recalculateSubtotal();
    }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) {
        this.rate = rate;
        recalculateSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public void recalculateSubtotal() {
        if (hours != null && rate != null) {
            this.subtotal = hours.multiply(rate);
        }
    }
}
