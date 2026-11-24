package com.workshop.model;

import java.sql.Timestamp;

public class Customer {
    private int id;
    private String customerCode;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String customerType; // INDIVIDUAL, CORPORATE
    private Timestamp createdAt;

    public Customer() {
        this.customerType = "INDIVIDUAL";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " (" + customerCode + ")";
    }
}
