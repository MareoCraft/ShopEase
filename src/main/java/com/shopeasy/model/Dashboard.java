
package com.shopeasy.model;

import java.util.List;

public class Dashboard {

    private int shopId;
    private String shopName;
    private String ownerName;
    private int totalUsers;

    private double todaySales;
    private double totalSales;
    private int totalProducts;
    private int lowStockProducts;
    private int totalCustomers;
    private double pendingUdhar;

    // Dashboard detail lists
    private List<Customer> pendingUdharCustomers;
    private List<Product> lowStockProductList;

    public Dashboard() {
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public double getTodaySales() {
        return todaySales;
    }

    public void setTodaySales(double todaySales) {
        this.todaySales = todaySales;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(int lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public double getPendingUdhar() {
        return pendingUdhar;
    }

    public void setPendingUdhar(double pendingUdhar) {
        this.pendingUdhar = pendingUdhar;
    }

    public List<Customer> getPendingUdharCustomers() {
        return pendingUdharCustomers;
    }

    public void setPendingUdharCustomers(List<Customer> pendingUdharCustomers) {
        this.pendingUdharCustomers = pendingUdharCustomers;
    }

    public List<Product> getLowStockProductList() {
        return lowStockProductList;
    }

    public void setLowStockProductList(List<Product> lowStockProductList) {
        this.lowStockProductList = lowStockProductList;
    }
}