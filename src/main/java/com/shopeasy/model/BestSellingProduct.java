
package com.shopeasy.model;

public class BestSellingProduct {

    private int productId;
    private String productName;
    private String sku;
    private double totalQuantitySold;
    private double totalSalesAmount;
    private int totalBills;

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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(double totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    public double getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(double totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public int getTotalBills() {
        return totalBills;
    }

    public void setTotalBills(int totalBills) {
        this.totalBills = totalBills;
    }
}