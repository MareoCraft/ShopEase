
package com.shopeasy.model;

import java.sql.Date;

public class Customer {

    private int customerId;
    private int shopId;
    private String customerName;
    private String mobile;
    private String email;
    private String address;
    private Date createdDate;
    private String status;

    // Total pending Udhar amount
    private double pendingUdhar;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPendingUdhar() {
        return pendingUdhar;
    }

    public void setPendingUdhar(double pendingUdhar) {
        this.pendingUdhar = pendingUdhar;
    }
}