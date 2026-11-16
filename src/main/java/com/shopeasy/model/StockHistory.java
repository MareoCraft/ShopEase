
package com.shopeasy.model;

import java.sql.Date;

public class StockHistory {

    private int historyId;
    private int shopId;
    private int productId;

    private String productName;
    private String movementType;

    private double quantityChange;
    private double previousQuantity;
    private double newQuantity;

    private String referenceType;
    private int referenceId;

    private String notes;
    private Date createdDate;

    public StockHistory() {
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public double getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(double quantityChange) {
        this.quantityChange = quantityChange;
    }

    public double getPreviousQuantity() {
        return previousQuantity;
    }

    public void setPreviousQuantity(double previousQuantity) {
        this.previousQuantity = previousQuantity;
    }

    public double getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(double newQuantity) {
        this.newQuantity = newQuantity;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
